package com.fintech.statementprocessor.adapter.in.rest.exception;

public class AccountNotFoundException extends BankStatementApiException {

    public AccountNotFoundException(String accountId) {
        super("Account not found: " + accountId);
    }
}
