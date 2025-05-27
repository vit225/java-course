package org.example.command;

import org.example.service.UserService;
import org.springframework.stereotype.Component;

@Component
public class ShowAllUserCommand implements OperationCommand {

    private final UserService userService;

    public ShowAllUserCommand(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void execute() {
        userService.getUsers();
    }

    @Override
    public ConsoleOperationType getOperationType() {
        return ConsoleOperationType.SHOW_ALL_USERS;
    }
}
