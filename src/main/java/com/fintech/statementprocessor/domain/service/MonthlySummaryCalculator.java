package com.fintech.statementprocessor.domain.service;

import com.fintech.statementprocessor.domain.model.MonthlySummary;
import com.fintech.statementprocessor.domain.model.Transaction;
import com.fintech.statementprocessor.domain.model.TransactionDirection;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

public class MonthlySummaryCalculator {

    public MonthlySummary calculate(
            String accountId,
            YearMonth month,
            List<Transaction> transactions) {

        BigDecimal income = BigDecimal.ZERO;
        BigDecimal spending = BigDecimal.ZERO;

        String currency = null;

        for (Transaction transaction : transactions) {

            currency = transaction.currency();

            if (transaction.direction() == TransactionDirection.CREDIT) {
                income = income.add(transaction.amount());
            }

            if (transaction.direction() == TransactionDirection.DEBIT) {
                spending = spending.add(transaction.amount());
            }
        }

        return new MonthlySummary(
                accountId,
                month,
                currency,
                income,
                spending,
                income.subtract(spending));
    }
}
