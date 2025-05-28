package org.example.exception;

public class AccountCloseException extends RuntimeException {
    public AccountCloseException(String message) {
        super(message);
    }
}