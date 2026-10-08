package com.fintech.statementprocessor.domain.service;

import com.fintech.statementprocessor.domain.model.MonthlySummary;
import com.fintech.statementprocessor.domain.model.Transaction;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import static com.fintech.statementprocessor.domain.model.TransactionCategory.*;
import static com.fintech.statementprocessor.domain.model.TransactionDirection.CREDIT;
import static com.fintech.statementprocessor.domain.model.TransactionDirection.DEBIT;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MonthlySummaryCalculatorTest {

    private final MonthlySummaryCalculator monthlySummaryCalculator =
            new MonthlySummaryCalculator();


    @Test
    void shouldCalculateIncomeSpendingAndBalance() {
        List<Transaction> transactions = List.of(
                new Transaction("txn-001", LocalDate.of(2026, 10, 2), BigDecimal.valueOf(1000),
                        "EUR", CREDIT, SALARY, "salary income"),
                new Transaction("txn-002", LocalDate.of(2026, 10, 3), BigDecimal.valueOf(500),
                        "EUR", CREDIT, OTHER_INCOME, "stock income"),
                new Transaction("txn-003", LocalDate.of(2026, 10, 3), BigDecimal.valueOf(120),
                        "EUR", DEBIT, ENTERTAINMENT, "netflix payment"),
                new Transaction("txn-004", LocalDate.of(2026, 10, 5), BigDecimal.valueOf(50),
                        "EUR", DEBIT, TRANSPORT, "HSL transport")
        );

        MonthlySummary summary = monthlySummaryCalculator.calculate("acc-xcv1", YearMonth.of(2026, 10), transactions);

        assertEquals(BigDecimal.valueOf(1500), summary.income());
        assertEquals(BigDecimal.valueOf(170), summary.spending());
        assertEquals(BigDecimal.valueOf(1330), summary.netMovement());
    }

}
