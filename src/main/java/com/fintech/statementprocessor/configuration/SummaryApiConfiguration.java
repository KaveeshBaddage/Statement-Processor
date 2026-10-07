package com.fintech.statementprocessor.configuration;

import com.fintech.statementprocessor.client.bank.invoker.ApiClient;
import com.fintech.statementprocessor.client.summary.api.MonthlySummaryApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SummaryApiConfiguration {

    @Bean
    public MonthlySummaryApi apiClient() {

        ApiClient apiClient = new ApiClient();

        apiClient.setBasePath(
                "https://xxxx.free.beeceptor.com");

        return new MonthlySummaryApi();
    }
}
