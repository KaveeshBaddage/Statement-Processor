package com.fintech.statementprocessor.domain.model;

import java.math.BigDecimal;
import java.util.List;

public record MonthlyTotals(
        BigDecimal income,
        BigDecimal spending) {

    public static MonthlyTotals empty() {
        return new MonthlyTotals(
                BigDecimal.ZERO,
                BigDecimal.ZERO);
    }

    public MonthlyTotals add(Transaction transaction) {

        if (transaction.direction() == TransactionDirection.CREDIT) {
            return new MonthlyTotals(
                    income.add(transaction.amount()),
                    spending);
        }

        if (transaction.direction() == TransactionDirection.DEBIT) {
            return new MonthlyTotals(
                    income,
                    spending.add(transaction.amount()));
        }

        return this;
    }

    public MonthlyTotals addAll(List<Transaction> transactions) {

        MonthlyTotals result = this;

        for (Transaction transaction : transactions) {
            result = result.add(transaction);
        }

        return result;
    }
}