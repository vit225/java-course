package org.example.service;

import org.example.AccountProperties;
import org.example.exception.UserAlreadyExistsException;
import org.example.exception.UserNotFoundException;
import org.example.model.Account;
import org.example.model.User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    private final Map<Integer, User> users = new HashMap<>();
    private static int counterId = 0;
    private final AccountProperties accountProperties;

    public UserService(AccountProperties accountProperties) {
        this.accountProperties = accountProperties;
    }

    public void createUser(String login) {
        if (users.values().stream().anyMatch(u -> u.getLogin().equals(login))) {
            throw new UserAlreadyExistsException("Пользователь с логином: " + login + " уже существует.");
        }
        int id = counterId++;
        User user = new User(id, login);
        users.put(id, user);
        Account account = new Account(AccountService.getCounterId(), id, accountProperties.getDefaultAmount());
        AccountService.addToAccounts(account);
        user.getAccountList().add(account);
        System.out.println("Пользователь с логином: " + login + " успешно создан ему присвоен id: " + id);
    }

    public User getUserById(int id) {
        User user = users.get(id);
        if (user == null) throw new UserNotFoundException("Пользователь с ID " + id + " не найден.");
        return user;
    }

    public void getUsers() {
        System.out.println("Список всех пользователей: " + users.values());
    }
}
