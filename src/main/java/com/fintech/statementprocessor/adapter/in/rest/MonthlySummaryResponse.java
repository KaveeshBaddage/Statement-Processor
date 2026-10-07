package com.fintech.statementprocessor.adapter.in.rest;

public record MonthlySummaryResponse(
        String accountId,
        String month,
        Boolean status) {
}
