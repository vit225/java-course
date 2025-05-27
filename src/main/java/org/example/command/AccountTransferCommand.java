package org.example.command;

import org.example.service.AccountService;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class AccountTransferCommand implements OperationCommand {

    private final AccountService accountService;

    public AccountTransferCommand(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public void execute() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите id счета с которого хотите сделать перевод:");
        int fromAccountId = Integer.parseInt(scanner.nextLine());
        System.out.println("Введите id счета куда хотите сделать перевод:");
        int toAccountId = Integer.parseInt(scanner.nextLine());
        System.out.println("Введите сумму перевода:");
        double amount = Double.parseDouble(scanner.nextLine());
        accountService.transfer(fromAccountId, toAccountId, amount);
    }

    @Override
    public ConsoleOperationType getOperationType() {
        return ConsoleOperationType.ACCOUNT_TRANSFER;
    }
}
