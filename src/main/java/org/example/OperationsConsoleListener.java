package org.example;

import org.example.service.AccountService;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Scanner;

@Component
public class OperationsConsoleListener {

    private final UserService userService;
    private final AccountService accountService;

    @Autowired
    public OperationsConsoleListener(UserService userService,
                                     AccountService accountService) {
        this.userService = userService;
        this.accountService = accountService;
    }

    public void startListening() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("""
                 Пожалуйста  введите одну из команд:
                 ACCOUNT_CREATE
                 SHOW_ALL_USERS
                 ACCOUNT_CLOSE
                 ACCOUNT_WITHDRAW
                 ACCOUNT_DEPOSIT
                 ACCOUNT_TRANSFER
                 USER_CREATE
                """);

        while (true) {
            String command = scanner.nextLine();
            try {
                switch (command) {
                    case "USER_CREATE" -> userCreate(scanner);
                    case "SHOW_ALL_USERS" -> showAllUsers();
                    case "ACCOUNT_CREATE" -> accountCreate(scanner);
                    case "ACCOUNT_CLOSE" -> accountClose(scanner);
                    case "ACCOUNT_DEPOSIT" -> accountDeposit(scanner);
                    case "ACCOUNT_WITHDRAW" -> accountWithdraw(scanner);
                    case "ACCOUNT_TRANSFER" -> accountTransfer(scanner);
                    default -> System.out.println("Команда введена неверно. Пожалуйста, попробуйте снова.");
                }
            } catch (RuntimeException e) {
                System.out.println("Ошибка при выполнении команды " + command + ": " + e.getMessage());
            }
            System.out.println("""
                    Пожалуйста  введите одну из команд:
                    ACCOUNT_CREATE
                    SHOW_ALL_USERS
                    ACCOUNT_CLOSE
                    ACCOUNT_WITHDRAW
                    ACCOUNT_DEPOSIT
                    ACCOUNT_TRANSFER
                    USER_CREATE""");
        }
    }

    private void userCreate(Scanner scanner) {
        System.out.println("Введите логин для нового пользователя:");
        String login = scanner.nextLine().trim();
        if (login.isEmpty()) {
            System.out.println("Логин не может быть пустым.");
            return;
        }
        userService.createUser(login);
    }

    private void showAllUsers() {
        userService.getUsers();
    }

    private void accountCreate(Scanner scanner) {
        System.out.println("Введите id пользователя, для которого вы хотите создать счет:");
        int userId = Integer.parseInt(scanner.nextLine().trim());
        userService.getUserById(userId);
        accountService.createAccountForUser(userId);
    }

    private void accountClose(Scanner scanner) {
        System.out.println("Введите id счета для закрытия:");
        int accountId = Integer.parseInt(scanner.nextLine().trim());
        accountService.closeAccount(accountId);
    }

    private void accountDeposit(Scanner scanner) {
        System.out.println("Введите id счета:");
        int accountId = Integer.parseInt(scanner.nextLine().trim());
        System.out.println("Введите сумму:");
        BigDecimal amount = new BigDecimal(scanner.nextLine().trim());
        accountService.deposit(accountId, amount);
        System.out.println("Сумма " + amount + " внесена на счет с id: " + accountId);
    }

    private void accountWithdraw(Scanner scanner) {
        System.out.println("Введите id счета для вывода средств:");
        int accountId = Integer.parseInt(scanner.nextLine().trim());
        System.out.println("Введите сумму для вывода средств:");
        double amount = Double.parseDouble(scanner.nextLine());
        accountService.withdraw(accountId, amount);
    }

    private void accountTransfer(Scanner scanner) {
        System.out.println("Введите id счета с которого хотите сделать перевод:");
        int fromAccountId = Integer.parseInt(scanner.nextLine());
        System.out.println("Введите id счета куда хотите сделать перевод:");
        int toAccountId = Integer.parseInt(scanner.nextLine());
        System.out.println("Введите сумму перевода:");
        double amount = Double.parseDouble(scanner.nextLine());
        accountService.transfer(fromAccountId, toAccountId, amount);
    }
}
