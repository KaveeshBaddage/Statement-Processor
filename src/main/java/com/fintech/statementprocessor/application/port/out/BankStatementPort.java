package com.fintech.statementprocessor.application.port.out;

import com.fintech.statementprocessor.domain.model.TransactionPage;

import java.time.YearMonth;

public interface BankStatementPort {

    TransactionPage getTransactions(
            String accountId,
            YearMonth month,
            int limit,
            String cursor);
}