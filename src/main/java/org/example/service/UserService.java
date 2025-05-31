package org.example.service;

import org.example.AccountProperties;
import org.example.TransactionHelper;
import org.example.exception.UserAlreadyExistsException;
import org.example.exception.UserNotFoundException;
import org.example.model.Account;
import org.example.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final TransactionHelper transactionHelper;
    private final SessionFactory sessionFactory;
    private final AccountProperties accountProperties;

    public UserService(TransactionHelper transactionHelper, SessionFactory sessionFactory, AccountProperties accountProperties) {
        this.transactionHelper = transactionHelper;
        this.sessionFactory = sessionFactory;
        this.accountProperties = accountProperties;
    }

    public void createUser(String login) {

        transactionHelper.executeInTransaction(() -> {
            Session session = sessionFactory.getCurrentSession();
            if (session.createQuery("SELECT u FROM User u WHERE login = :login", User.class)
                    .setParameter("login", login)
                    .uniqueResult() != null) {
                throw new UserAlreadyExistsException("Пользователь с логином: " + login + " уже существует.");
            }
            User user = new User(login);
            session.persist(user);
            Account account = new Account(user, accountProperties.getDefaultAmount());
            user.addAccount(account);
            System.out.println("Пользователь с логином: " + login + " успешно создан.");
            return null;
        });
    }

    public void getUserById(int id) {
        try (Session session = sessionFactory.openSession()) {
            User user = session.get(User.class, id);
            if (user == null) {
                throw new UserNotFoundException("Пользователь с ID " + id + " не найден.");
            }
        }
    }

    public void getUsers() {
        try (Session session = sessionFactory.openSession()) {
            System.out.println("Все пользователи: " + session.createQuery("FROM User", User.class).list());
        }
    }
}
