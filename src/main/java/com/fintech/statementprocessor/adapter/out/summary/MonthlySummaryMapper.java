package com.fintech.statementprocessor.adapter.out.summary;

import org.springframework.stereotype.Component;


import com.fintech.statementprocessor.domain.model.MonthlySummary;



@Component
public class MonthlySummaryMapper {

    public com.fintech.statementprocessor.client.summary.model.MonthlySummary toApiModel(
            MonthlySummary summary) {

        var request =
                new com.fintech.statementprocessor.client.summary.model.MonthlySummary();

        request.setAccountId(summary.accountId());
        request.setMonth(summary.month().toString());
        request.setCurrency(summary.currency());
        request.setIncome(summary.income().doubleValue());
        request.setSpending(summary.spending().doubleValue());
        request.setBalance(summary.netMovement().doubleValue());

        return request;
    }
}