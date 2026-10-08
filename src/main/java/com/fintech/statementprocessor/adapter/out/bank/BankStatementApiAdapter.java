package com.fintech.statementprocessor.adapter.out.bank;

import com.fintech.statementprocessor.adapter.in.rest.exception.AccountNotFoundException;
import com.fintech.statementprocessor.adapter.in.rest.exception.BankStatementApiException;
import com.fintech.statementprocessor.adapter.in.rest.exception.BankStatementApiUnavailableException;
import com.fintech.statementprocessor.application.port.out.BankStatementPort;
import com.fintech.statementprocessor.client.bank.api.StatementsApi;
import com.fintech.statementprocessor.domain.model.TransactionPage;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.time.YearMonth;

@Component
public class BankStatementApiAdapter implements BankStatementPort {

    private final StatementsApi statementsApi;
    private final BankStatementMapper mapper;

    public BankStatementApiAdapter(StatementsApi statementsApi, BankStatementMapper mapper) {
        this.statementsApi = statementsApi;
        this.mapper = mapper;
    }

    @Override
    public TransactionPage getTransactions(String accountId, YearMonth month, int limit, String cursor) {

        try {
            var response = statementsApi.getMonthlyStatement(
                    accountId,
                    month.toString(),
                    limit,
                    cursor);

            return mapper.toDomain(response);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new AccountNotFoundException(accountId);
        } catch (HttpClientErrorException ex) {
            throw new BankStatementApiException("Bank API returned HTTP " + ex.getStatusCode(), ex);
        } catch (HttpServerErrorException ex) {
            throw new BankStatementApiUnavailableException("Bank API is unavailable", ex);
        }
    }
}
