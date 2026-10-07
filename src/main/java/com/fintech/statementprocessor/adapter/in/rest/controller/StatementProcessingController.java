package com.fintech.statementprocessor.adapter.in.rest.controller;

import com.fintech.statementprocessor.adapter.in.rest.MonthlySummaryResponse;
import com.fintech.statementprocessor.application.port.in.MonthlySummaryResult;
import com.fintech.statementprocessor.application.port.in.ProcessMonthlyStatementUseCase;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;

@RestController
@RequestMapping("/api/v1/accounts")
public class StatementProcessingController {

    private final ProcessMonthlyStatementUseCase useCase;

    public StatementProcessingController(
            ProcessMonthlyStatementUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping(
            "/{accountId}/monthly-summary/{month}")
    public ResponseEntity<MonthlySummaryResponse> process(
            @PathVariable String accountId,
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM") YearMonth month) {

        MonthlySummaryResult result =
                useCase.process(accountId, month);

        return ResponseEntity.ok(
                new MonthlySummaryResponse(
                        result.accountId(),
                        result.month().toString(),
                        result.processed()));
    }
}