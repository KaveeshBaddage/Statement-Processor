package com.fintech.statementprocessor.configuration;

import com.fintech.statementprocessor.client.bank.api.StatementsApi;
import com.fintech.statementprocessor.client.bank.invoker.ApiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BankApiConfiguration {

    private static final Logger log =
            LoggerFactory.getLogger(BankApiConfiguration.class);

    @Bean
    public ApiClient bankApiClient(
            @Value("${bank.api.base-url}") String baseUrl) {

        log.info("Bank Statement API base URL: {}", baseUrl);

        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath(baseUrl);

        return apiClient;
    }

    @Bean
    public StatementsApi statementsApi(ApiClient bankApiClient) {
        return new StatementsApi(bankApiClient);
    }
}
