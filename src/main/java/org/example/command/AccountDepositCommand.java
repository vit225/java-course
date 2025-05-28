package org.example.command;

import org.example.service.AccountService;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class AccountDepositCommand implements OperationCommand {

    private final AccountService accountService;

    public AccountDepositCommand(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public void execute() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите id счета:");
        int accountId = Integer.parseInt(scanner.nextLine().trim());
        System.out.println("Введите сумму:");
        double amount = Double.parseDouble(scanner.nextLine());
        accountService.deposit(accountId, amount);
    }

    @Override
    public ConsoleOperationType getOperationType() {
        return ConsoleOperationType.ACCOUNT_DEPOSIT;
    }
}
