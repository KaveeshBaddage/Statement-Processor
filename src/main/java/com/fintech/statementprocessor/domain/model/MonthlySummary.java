package com.fintech.statementprocessor.domain.model;

import java.math.BigDecimal;
import java.time.YearMonth;

public record MonthlySummary(
        String accountId,
        YearMonth month,
        String  currency,
        BigDecimal income,
        BigDecimal spending,
        BigDecimal netMovement) {
}
