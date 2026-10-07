package com.fintech.statementprocessor.domain.service;

import com.fintech.statementprocessor.application.port.in.MonthlySummaryResult;
import com.fintech.statementprocessor.application.port.in.ProcessMonthlyStatementUseCase;
import com.fintech.statementprocessor.application.port.out.BankStatementPort;
import com.fintech.statementprocessor.application.port.out.MonthlySummaryPort;
import com.fintech.statementprocessor.domain.model.MonthlySummary;
import com.fintech.statementprocessor.domain.model.MonthlyTotals;
import com.fintech.statementprocessor.domain.model.TransactionPage;
import org.springframework.stereotype.Service;

import java.time.YearMonth;

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
            YearMonth month) {

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

        monthlySummaryPort.submitSummary(summary);

        return new MonthlySummaryResult(
                accountId,
                month,
                true);
    }
}
