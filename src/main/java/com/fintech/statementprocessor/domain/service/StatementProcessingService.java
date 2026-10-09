package com.fintech.statementprocessor.domain.service;

import com.fintech.statementprocessor.application.port.in.MonthlySummaryResult;
import com.fintech.statementprocessor.application.port.in.ProcessMonthlyStatementUseCase;
import com.fintech.statementprocessor.application.port.out.BankStatementPort;
import com.fintech.statementprocessor.application.port.out.MonthlySummaryPort;
import com.fintech.statementprocessor.domain.model.MonthlySummary;
import com.fintech.statementprocessor.domain.model.MonthlyTotals;
import com.fintech.statementprocessor.domain.model.TransactionPage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.YearMonth;

@Slf4j
@Service
public class StatementProcessingService
        implements ProcessMonthlyStatementUseCase {

    private static final int PAGE_SIZE = 100;

    private final BankStatementPort bankStatementPort;
    private final MonthlySummaryPort monthlySummaryPort;

    public StatementProcessingService(
            BankStatementPort bankStatementPort,
            MonthlySummaryPort monthlySummaryPort) {

        this.bankStatementPort = bankStatementPort;
        this.monthlySummaryPort = monthlySummaryPort;
    }

    @Override
    public MonthlySummaryResult process(
            String accountId,
            YearMonth month, String requestId) {

        log.info("Starting monthly summary calculation:  accountId={}, month={}", accountId, month);

        MonthlyTotals totals = MonthlyTotals.empty();

        String cursor = null;

        String currency;


        do {
            TransactionPage page =
                    bankStatementPort.getTransactions(
                            accountId,
                            month,
                            PAGE_SIZE,
                            cursor);

            totals = totals.addAll(page.transactions());

            cursor = page.nextCursor();

            log.debug(
                    "Fetched transaction page:  accountId={}, month={}, cursor={}, transactionCount={}",
                    accountId, month, cursor, page.transactions().size()
            );


            currency = page.currency();


        } while (cursor != null);

        MonthlySummary summary =
                new MonthlySummary(
                        accountId,
                        month,
                        currency,
                        totals.income(),
                        totals.spending(),
                        totals.income().subtract(
                                totals.spending()));

        log.info(
                "Monthly summary calculated: accountId={}, month={}, currency={}, , income={}, spending={}, net={}",
                accountId,
                month,
                currency,
                summary.income(),
                summary.spending(),
                summary.income().subtract(summary.spending())
        );

        monthlySummaryPort.submitSummary(summary, requestId);

        log.info("Monthly summary submitted successfully: accountId={}, month={}", accountId, month);

        return new MonthlySummaryResult(
                accountId,
                month,
                true);
    }
}
