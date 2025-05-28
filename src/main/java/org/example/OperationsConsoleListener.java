package org.example;

import org.example.command.ConsoleOperationType;
import org.example.command.OperationCommand;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

@Component
public class OperationsConsoleListener implements Runnable {

    private final Map<ConsoleOperationType, OperationCommand> commandMap = new HashMap<>();

    public OperationsConsoleListener(List<OperationCommand> commands) {
        commands.forEach(command -> commandMap.put(command.getOperationType(), command));
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
                ConsoleOperationType operationType = ConsoleOperationType.valueOf(command);
                executeCommand(operationType);
            } catch (IllegalArgumentException e) {
                System.out.println("Команда введена неверно. Пожалуйста, попробуйте снова.");
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

    @Override
    public void run() {
        startListening();
    }

    public void executeCommand(ConsoleOperationType operationType) {
        OperationCommand command = commandMap.get(operationType);
        if (command != null) {
            command.execute();
        } else {
            System.out.println("Команда не найдена: " + operationType);
        }
    }
}
