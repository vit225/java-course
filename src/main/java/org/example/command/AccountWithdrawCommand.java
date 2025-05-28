package org.example.command;

import org.example.service.AccountService;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class AccountWithdrawCommand implements OperationCommand {

    private final AccountService accountService;

    public AccountWithdrawCommand(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public void execute() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите id счета для вывода средств:");
        int accountId = Integer.parseInt(scanner.nextLine().trim());
        System.out.println("Введите сумму для вывода средств:");
        double amount = Double.parseDouble(scanner.nextLine());
        accountService.withdraw(accountId, amount);
    }

    @Override
    public ConsoleOperationType getOperationType() {
        return ConsoleOperationType.ACCOUNT_WITHDRAW;
    }
}
