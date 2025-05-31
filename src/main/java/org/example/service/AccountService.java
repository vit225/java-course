package org.example.service;

import org.example.AccountProperties;
import org.example.TransactionHelper;
import org.example.exception.AccountCloseException;
import org.example.exception.AccountNotFoundException;
import org.example.exception.InsufficientFundsException;
import org.example.exception.UserNotFoundException;
import org.example.model.Account;
import org.example.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class AccountService {

    private final TransactionHelper transactionHelper;
    private final SessionFactory sessionFactory;
    private final AccountProperties accountProperties;

    public AccountService(TransactionHelper transactionHelper, SessionFactory sessionFactory, AccountProperties accountProperties) {
        this.transactionHelper = transactionHelper;
        this.sessionFactory = sessionFactory;
        this.accountProperties = accountProperties;
    }

    public void createAccountForUser(int userId) {

        transactionHelper.executeInTransaction(() -> {
            Session session = sessionFactory.getCurrentSession();

            User user = session.get(User.class, userId);
            if (user == null) {
                throw new UserNotFoundException("Пользователь с id: " + userId + " не найден");
            }
            Account newAccount = new Account(user, accountProperties.getDefaultAmount());
            user.addAccount(newAccount);
            session.persist(newAccount);
            System.out.println("Создан новый счет: " + newAccount);
            return null;
        });
    }

    public void deposit(int accountId, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Сумма пополнения должна быть положительной.");
        }

        transactionHelper.executeInTransaction(() -> {
            Session session = sessionFactory.getCurrentSession();

            Account account = session.get(Account.class, accountId);
            if (account == null) {
                throw new AccountNotFoundException("Аккаунт с id " + accountId + " не найден.");
            }
            account.setMoneyAmount(account.getMoneyAmount() + amount);
            session.merge(account);
            System.out.println("Счет с id " + accountId + " пополнен на сумму " + amount);
            return null;
        });
    }

    public void withdraw(int accountId, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Сумма для снятия должна быть положительной.");
        }

        transactionHelper.executeInTransaction(() -> {
            Session session = sessionFactory.getCurrentSession();

            Account account = session.get(Account.class, accountId);
            if (account == null) {
                throw new AccountNotFoundException("Счет с ID " + accountId + " не найден.");
            }
            if (account.getMoneyAmount() < amount) {
                throw new InsufficientFundsException("Недостаточно средств на счету ID " + accountId);
            }
            account.setMoneyAmount(account.getMoneyAmount() - amount);
            session.merge(account);
            System.out.println("Сумма " + amount + " успешно снята со счета ID " + accountId);
            return null;
        });
    }

    public void transfer(int fromAccountId, int toAccountId, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Сумма для перевода должна быть положительной.");
        }

        transactionHelper.executeInTransaction(() -> {
            Session session = sessionFactory.getCurrentSession();

            Account fromAccount = session.get(Account.class, fromAccountId);
            Account toAccount = session.get(Account.class, toAccountId);

            if (fromAccount == null) {
                throw new AccountNotFoundException("Счет отправителя с ID " + fromAccountId + " не найден.");
            }
            if (toAccount == null) {
                throw new AccountNotFoundException("Счет получателя с ID " + toAccountId + " не найден.");
            }

            if (fromAccount.getMoneyAmount() < amount) {
                throw new InsufficientFundsException("Недостаточно средств на счету ID " + fromAccountId +
                        ". Доступная сумма: " + fromAccount.getMoneyAmount());
            }

            boolean differentUsers = !Objects.equals(fromAccount.getUser().getId(), toAccount.getUser().getId());
            double commission = 0.0;

            if (differentUsers) {
                commission = accountProperties.getTransferCommission();
            }

            double totalDebit = amount + commission;

            if (fromAccount.getMoneyAmount() < totalDebit) {
                throw new InsufficientFundsException("Ошибка: недостаточно средств на счету ID " + fromAccountId +
                        " для перевода с учётом комиссии. Доступная сумма: " + fromAccount.getMoneyAmount() +
                        ", Требуется: " + totalDebit);
            }

            fromAccount.setMoneyAmount(fromAccount.getMoneyAmount() - totalDebit);
            toAccount.setMoneyAmount(toAccount.getMoneyAmount() + amount);

            session.merge(fromAccount);
            session.merge(toAccount);

            System.out.println("Сумма " + amount + " успешно переведена со счета ID " + fromAccountId +
                    " на счет ID " + toAccountId);
            return null;
        });
    }

    public void closeAccount(int accountId) {

        transactionHelper.executeInTransaction(() -> {
            Session session = sessionFactory.getCurrentSession();
            Account accountClose = session.get(Account.class, accountId);
            if (accountClose == null) {
                throw new AccountNotFoundException("Счет с id: " + accountId + " не найден");
            }

            User user = accountClose.getUser();
            if (user.getAccountList().size() == 1) {
                throw new AccountCloseException("Нельзя закрыть последний счет пользователя");
            }
            double remnant = accountClose.getMoneyAmount();


            if (remnant > 0) {
                List<Account> accountList = user.getAccountList();

                Optional<Account> firstAccount = accountList.stream()
                        .filter(account -> account.getId() != accountId)
                        .findFirst();
                if (firstAccount.isPresent()) {
                    Account firstAcc = firstAccount.get();

                    firstAcc.setMoneyAmount(firstAcc.getMoneyAmount() + accountClose.getMoneyAmount());
                }
            }

            user.getAccountList().remove(accountClose);
            session.remove(accountClose);

            System.out.println("Счет с id: " + accountId + " успешно закрыт");
            return null;
        });
    }
}