package com.fintech.statementprocessor.domain.model;

import java.time.YearMonth;
import java.util.List;

public record TransactionPage(
        String accountId,
        YearMonth month,
        String currency,
        List<Transaction> transactions,
        String nextCursor,
        boolean hasMore) {
}
