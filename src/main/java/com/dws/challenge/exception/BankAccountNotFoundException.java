package com.dws.challenge.exception;

public class BankAccountNotFoundException extends RuntimeException {
    public BankAccountNotFoundException(String s) {
        super(s);
    }
}
