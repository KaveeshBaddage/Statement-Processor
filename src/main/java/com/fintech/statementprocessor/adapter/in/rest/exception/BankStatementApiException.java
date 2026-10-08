package com.fintech.statementprocessor.adapter.in.rest.exception;

public class BankStatementApiException extends RuntimeException {

    public BankStatementApiException(String message) {
        super(message);
    }

    public BankStatementApiException(String message, Throwable cause) {
        super(message, cause);
    }
}