package org.example.command;

import org.example.service.UserService;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class CreateUserCommand implements OperationCommand {

    private final UserService userService;

    public CreateUserCommand(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void execute() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите логин для нового пользователя:");
        String login = scanner.nextLine().trim();
        if (login.isEmpty()) {
            System.out.println("Логин не может быть пустым.");
            return;
        }
        userService.createUser(login);
    }

    @Override
    public ConsoleOperationType getOperationType() {
        return ConsoleOperationType.USER_CREATE;
    }
}
