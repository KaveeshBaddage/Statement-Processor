package com.fintech.statementprocessor.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record Transaction(
        String transactionId,
        LocalDate date,
        BigDecimal amount,
        String currency,
        TransactionDirection direction,
        TransactionCategory category,
        String description) {
}
