package com.fintech.statementprocessor.application.port.in;

import java.time.YearMonth;

public record MonthlySummaryResult(
        String accountId,
        YearMonth month,
        boolean processed) {
}
