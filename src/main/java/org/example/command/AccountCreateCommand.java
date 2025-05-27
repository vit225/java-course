package org.example.command;

import org.example.service.AccountService;
import org.example.service.UserService;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class AccountCreateCommand implements OperationCommand {

    private final UserService userService;
    private final AccountService accountService;

    public AccountCreateCommand(UserService userService, AccountService accountService) {
        this.userService = userService;
        this.accountService = accountService;
    }

    @Override
    public void execute() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите id пользователя, для которого вы хотите создать счет:");
        int userId = Integer.parseInt(scanner.nextLine().trim());
        userService.getUserById(userId);
        accountService.createAccountForUser(userId);
    }

    @Override
    public ConsoleOperationType getOperationType() {
        return ConsoleOperationType.ACCOUNT_CREATE;
    }
}
