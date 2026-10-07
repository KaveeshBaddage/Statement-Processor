package com.fintech.statementprocessor.adapter.out.bank;

import com.fintech.statementprocessor.application.port.out.BankStatementPort;
import com.fintech.statementprocessor.domain.model.TransactionPage;
import com.fintech.statementprocessor.client.bank.api.StatementsApi;
import org.springframework.stereotype.Component;

import java.time.YearMonth;

@Component
public class BankStatementApiAdapter  implements BankStatementPort {

    private final StatementsApi statementsApi;
    private final BankStatementMapper mapper;

    public BankStatementApiAdapter(StatementsApi statementsApi, BankStatementMapper mapper) {
        this.statementsApi = statementsApi;
        this.mapper = mapper;
    }

    @Override
    public TransactionPage getTransactions(String accountId, YearMonth month, int limit, String cursor) {
        var response = statementsApi.getMonthlyStatement(
                accountId,
                month.toString(),
                limit,
                cursor);

        return mapper.toDomain(response);
    }
}
