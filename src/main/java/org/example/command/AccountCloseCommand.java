package org.example.command;

import org.example.service.AccountService;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class AccountCloseCommand implements OperationCommand {

    private final AccountService accountService;

    public AccountCloseCommand(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public void execute() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите id счета для закрытия:");
        int accountId = Integer.parseInt(scanner.nextLine().trim());
        accountService.closeAccount(accountId);
    }

    @Override
    public ConsoleOperationType getOperationType() {
        return ConsoleOperationType.ACCOUNT_CLOSE;
    }
}
