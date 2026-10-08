package com.fintech.statementprocessor.adapter.out.summary;

import com.fintech.statementprocessor.adapter.out.exception.SummaryApiException;
import com.fintech.statementprocessor.adapter.out.exception.SummaryApiUnavailableException;
import com.fintech.statementprocessor.application.port.out.MonthlySummaryPort;
import com.fintech.statementprocessor.client.summary.api.MonthlySummaryApi;
import com.fintech.statementprocessor.domain.model.MonthlySummary;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.util.UUID;

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

        String idempotencyKey = String.join(":", request.getAccountId(), request.getMonth());

        try {
            monthlySummaryApi.createMonthlySummary(
                    idempotencyKey,
                    request, UUID.randomUUID());
        } catch (HttpClientErrorException ex) {
            throw new SummaryApiException("Summery API returned HTTP " + ex.getStatusCode(), ex);
        } catch (HttpServerErrorException ex) {
            throw new SummaryApiUnavailableException("Summery API is unavailable", ex);
        }
    }
}
