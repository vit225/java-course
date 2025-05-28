package org.example.command;

public interface OperationCommand {
    void execute();

    ConsoleOperationType getOperationType();
}
