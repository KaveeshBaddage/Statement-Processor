package com.fintech.statementprocessor.adapter.out.summary;

import com.fintech.statementprocessor.application.port.out.MonthlySummaryPort;
import com.fintech.statementprocessor.domain.model.MonthlySummary;
import com.fintech.statementprocessor.client.summary.api.MonthlySummaryApi;

import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class MonthlySummaryApiAdapter
        implements MonthlySummaryPort {

    private final MonthlySummaryApi monthlySummaryApi;
    private final MonthlySummaryMapper mapper;

    public MonthlySummaryApiAdapter(
            MonthlySummaryApi monthlySummaryApi,
            MonthlySummaryMapper mapper) {

        this.monthlySummaryApi = monthlySummaryApi;
        this.mapper = mapper;
    }

    @Override
    public void submitSummary(MonthlySummary summary) {

        var request = mapper.toApiModel(summary);

        String idempotencyKey = UUID.randomUUID().toString();

        monthlySummaryApi.createMonthlySummary(
                idempotencyKey,
                request, UUID.randomUUID());
    }
}
