package org.gstroke.exception;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(String number) {
        super("Account not found: " + number);
    }
}