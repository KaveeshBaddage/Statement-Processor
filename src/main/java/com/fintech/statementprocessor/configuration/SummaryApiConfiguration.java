package com.fintech.statementprocessor.configuration;

import com.fintech.statementprocessor.client.summary.invoker.ApiClient;
import com.fintech.statementprocessor.client.summary.api.MonthlySummaryApi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test")
public class SummaryApiConfiguration {

    private static final Logger log =
            LoggerFactory.getLogger(SummaryApiConfiguration.class);

    @Bean
    public ApiClient summeryApiClient(@Value("${summary.api.base-url}") String baseUrl) {

        log.info("Summary API base URL: {}", baseUrl);

        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath(baseUrl);

        return apiClient;
    }

    @Bean
    public MonthlySummaryApi monthlySummaryApi(ApiClient summaryApiClient) {
        return new MonthlySummaryApi(summaryApiClient);
    }
}

