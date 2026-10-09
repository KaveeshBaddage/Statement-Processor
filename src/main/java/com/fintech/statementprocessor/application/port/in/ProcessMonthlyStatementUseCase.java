package com.fintech.statementprocessor.application.port.in;

import java.time.YearMonth;

public interface ProcessMonthlyStatementUseCase {

    MonthlySummaryResult process(
            String accountId,
            YearMonth month,
            String requestId);
}

