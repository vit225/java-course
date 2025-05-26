package org.example.service;

import org.example.AccountProperties;
import org.example.exception.AccountCloseException;
import org.example.exception.AccountNotFoundException;
import org.example.exception.InsufficientFundsException;
import org.example.exception.UserNotFoundException;
import org.example.model.Account;
import org.example.model.User;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AccountService {

    private static final Map<Integer, Account> accounts = new HashMap<>();
    private static int counterId;
    private final AccountProperties accountProperties;
    private final UserService userService;

    public AccountService(AccountProperties accountProperties, UserService userService) {
        this.accountProperties = accountProperties;
        this.userService = userService;
    }

    public static int getCounterId() {
        return counterId++;
    }

    public static void addToAccounts(Account account) {
        accounts.put(account.getId(), account);
    }

    public void createAccountForUser(int userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            throw new UserNotFoundException("Пользователь с id: " + userId + " не найден");
        }

        int id = counterId++;
        Account newAccount = new Account(id, userId, accountProperties.getDefaultAmount());
        accounts.put(id, newAccount);
        user.getAccountList().add(newAccount);
        System.out.println("Создан новый счет: " + newAccount);
    }

    public void deposit(int accountId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Сумма пополнения должна быть положительной.");
        }

        for (Account account : accounts.values()) {
            if (account.getId() == accountId) {
                account.setMoneyAmount(account.getMoneyAmount());
                System.out.println("Счет с id " + accountId + " пополнен на сумму " + amount);
                return;

            }
        }
        throw new AccountNotFoundException("Аккаунт с id " + accountId + " не найден.");
    }

    public void withdraw(int accountId, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Сумма для снятия должна быть положительной.");
        }
        for (Account account : accounts.values()) {
            if (account.getId() == accountId) {
                if (account.getMoneyAmount() < 0) {
                    throw new InsufficientFundsException("Недостаточно средств на счету ID " + accountId +
                            ". Доступная сумма: " + account.getMoneyAmount());
                }
                account.setMoneyAmount(account.getMoneyAmount() - amount);
                System.out.println("Сумма " + amount + " успешно снята со счета ID " + accountId);
                return;
            }
        }
        throw new AccountNotFoundException("Счет с ID " + accountId + " не найден.");
    }


    public void transfer(int fromAccountId, int toAccountId, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Сумма для перевода должна быть положительной.");
        }

        Account fromAccount = null;
        Account toAccount = null;

        for (Account account : accounts.values()) {
            if (account.getId() == fromAccountId) {
                fromAccount = account;
            }
            if (account.getId() == toAccountId) {
                toAccount = account;

            }
        }

        if (fromAccount == null) {
            throw new AccountNotFoundException("Счет отправителя с ID " + fromAccountId + " не найден.");
        }
        if (toAccount == null) {
            throw new AccountNotFoundException("Счет получателя с ID " + toAccountId + " не найден.");
        }

        if (fromAccount.getMoneyAmount() < 0) {
            throw new InsufficientFundsException("Недостаточно средств на счету ID " + fromAccountId +
                    ". Доступная сумма: " + fromAccount.getMoneyAmount());
        }

        boolean differentUsers = fromAccount.getUserId() != toAccount.getUserId();
        double commission = 0.0;

        if (differentUsers) {
            commission = accountProperties.getTransferCommission();
        }

        double totalDebit = amount + commission;

        if (fromAccount.getMoneyAmount() < 0) {
            throw new InsufficientFundsException("Ошибка: недостаточно средств на счету ID " + fromAccountId +
                    " для перевода с учётом комиссии. Доступная сумма: " + fromAccount.getMoneyAmount() +
                    ", Требуется: " + totalDebit);
        }

        fromAccount.setMoneyAmount(fromAccount.getMoneyAmount() - totalDebit);
        toAccount.setMoneyAmount(toAccount.getMoneyAmount() + amount);

        System.out.println("Сумма " + amount + " успешно переведена со счета ID " + fromAccountId +
                " на счет ID " + toAccountId);
    }

    public void closeAccount(int accountId) {
        Account accountClose = null;
        for (Account account : accounts.values()) {
            if (account.getId() == accountId) {
                accountClose = account;
            }
        }

        if (accountClose == null) {
            throw new AccountNotFoundException("Счет с id: " + accountId + " не найден");
        }

        User user = userService.getUserById(accountClose.getUserId());
        List<Account> accountList = user.getAccountList();

        if (accountList.size() == 1) {
            throw new AccountCloseException("Нельзя закрыть последний счет пользователя");
        }

        double remnant = accountClose.getMoneyAmount();

        if (remnant > 0) {
            Optional<Account> firstAccount = accountList.stream()
                    .filter(account -> account.getId() != accountId)
                    .findFirst();
            if (firstAccount.isPresent()) {
                Account firstAcc = firstAccount.get();

                firstAcc.setMoneyAmount(firstAcc.getMoneyAmount());
            }
        }

        accounts.remove(accountId);
        accountList.remove(accountClose);
        System.out.println("Счет с id: " + accountId + " успешно закрыт");
    }
}


