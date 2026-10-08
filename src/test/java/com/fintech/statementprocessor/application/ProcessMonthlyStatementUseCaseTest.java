package com.fintech.statementprocessor.application;

import com.fintech.statementprocessor.application.port.out.BankStatementPort;
import com.fintech.statementprocessor.application.port.out.MonthlySummaryPort;
import com.fintech.statementprocessor.domain.model.MonthlySummary;
import com.fintech.statementprocessor.domain.model.Transaction;
import com.fintech.statementprocessor.domain.model.TransactionPage;
import com.fintech.statementprocessor.domain.service.StatementProcessingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import static com.fintech.statementprocessor.domain.model.TransactionCategory.*;
import static com.fintech.statementprocessor.domain.model.TransactionCategory.TRANSPORT;
import static com.fintech.statementprocessor.domain.model.TransactionDirection.CREDIT;
import static com.fintech.statementprocessor.domain.model.TransactionDirection.DEBIT;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProcessMonthlyStatementUseCaseTest {

    @Mock
    BankStatementPort bankStatementPort;

    @Mock
    MonthlySummaryPort monthlySummaryPort;


    @InjectMocks
    StatementProcessingService useCase;


    @Test
    void shouldSubmitCalculatedSummary() {

        List<Transaction> transactions = List.of(
                new Transaction("txn-001", LocalDate.of(2026,10,2), BigDecimal.valueOf(1000),
                        "EUR", CREDIT, SALARY, "salary income"  ),
                new Transaction("txn-004", LocalDate.of(2026,10,5), BigDecimal.valueOf(50),
                        "EUR", DEBIT, TRANSPORT, "HSL transport"  )
        );

        when(bankStatementPort.getTransactions("test-accountId", YearMonth.of(2026,10),
                100, null))
                .thenReturn(new TransactionPage("test-accountId", YearMonth.of(2026,10),
                        "EUR", transactions, null, false  ));

        useCase.process("test-accountId", YearMonth.of(2026,10));


        verify(monthlySummaryPort)
                .submitSummary(new MonthlySummary("test-accountId", YearMonth.of(2026,10),
                        "EUR", BigDecimal.valueOf(1000) ,  BigDecimal.valueOf(50),  BigDecimal.valueOf(950)));
    }

    @Test
    void shouldProcessAllPagesUntilCursorIsNull() {

        Transaction salary =
                new Transaction(
                        "txn-001",
                        LocalDate.of(2026, 10, 2),
                        BigDecimal.valueOf(1000),
                        "EUR",
                        CREDIT,
                        SALARY,
                        "Salary");

        Transaction groceries =
                new Transaction(
                        "txn-002",
                        LocalDate.of(2026, 10, 5),
                        BigDecimal.valueOf(200),
                        "EUR",
                        DEBIT,
                        GROCERIES,
                        "Supermarket");

        Transaction transport =
                new Transaction(
                        "txn-003",
                        LocalDate.of(2026, 10, 8),
                        BigDecimal.valueOf(50),
                        "EUR",
                        DEBIT,
                        TRANSPORT,
                        "HSL");

        when(bankStatementPort.getTransactions(
                "account-12345",
                YearMonth.of(2026, 10),
                100,
                null))
                .thenReturn(
                        new TransactionPage(
                                "account-12345",
                                YearMonth.of(2026, 10),
                                "EUR",
                                List.of(salary, groceries),
                                "cursor-2",
                                true));

        when(bankStatementPort.getTransactions(
                "account-12345",
                YearMonth.of(2026, 10),
                100,
                "cursor-2"))
                .thenReturn(
                        new TransactionPage(
                                "account-12345",
                                YearMonth.of(2026, 10),
                                "EUR",
                                List.of(transport),
                                null,
                                false));

        useCase.process(
                "account-12345",
                YearMonth.of(2026, 10));

        verify(bankStatementPort, times(2))
                .getTransactions(
                        anyString(),
                        any(),
                        eq(100),
                        any());

        verify(monthlySummaryPort)
                .submitSummary(
                        new MonthlySummary(
                                "account-12345",
                                YearMonth.of(2026, 10),
                                "EUR",
                                BigDecimal.valueOf(1000),
                                BigDecimal.valueOf(250),
                                BigDecimal.valueOf(750)));
    }
}
