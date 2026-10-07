package com.fintech.statementprocessor.adapter.out.bank;


import com.fintech.statementprocessor.domain.model.Transaction;
import com.fintech.statementprocessor.domain.model.TransactionCategory;
import com.fintech.statementprocessor.domain.model.TransactionDirection;
import com.fintech.statementprocessor.domain.model.TransactionPage;
import org.springframework.stereotype.Component;

import java.time.YearMonth;

@Component
public class BankStatementMapper {

    public TransactionPage toDomain(
            com.fintech.statementprocessor.client.bank.model.MonthlyStatement source) {

        var transactions = source.getTransactions()
                .stream()
                .map(this::toDomain)
                .toList();

        return new TransactionPage(
                source.getAccountId(),
                YearMonth.parse(source.getMonth()),
                source.getCurrency(),
                transactions,
                source.getNextCursor(),
                source.getHasMore());
    }

    private Transaction toDomain(
            com.fintech.statementprocessor.client.bank.model.Transaction source) {

        return new Transaction(
                source.getTransactionId(),
                source.getDate(),
                source.getAmount(),
                source.getCurrency(),
                TransactionDirection.valueOf(
                        source.getDirection().getValue()),
                TransactionCategory.valueOf(
                        source.getCategory().getValue()),
                source.getDescription());
    }
}
