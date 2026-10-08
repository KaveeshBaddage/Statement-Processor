package com.fintech.statementprocessor.configuration;

import com.fintech.statementprocessor.client.summary.api.MonthlySummaryApi;
import com.fintech.statementprocessor.client.summary.invoker.ApiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;

@Configuration
@Profile("test")
public class SummaryApiTestConfiguration {

    private static final Logger log =
            LoggerFactory.getLogger(SummaryApiConfiguration.class);

    @Bean
    public ApiClient summaryApiClient(
            @Value("${summary.api.base-url}") String baseUrl) {

        log.info("Summary API base URL: {}", baseUrl);


        //not let HTTP client to attempt an h2c upgrade when communicating with this WireMock test server, to avoid connection ended unexpectedly
        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        RestClient restClient =
                ApiClient.buildRestClientBuilder()
                        .requestFactory(
                                new JdkClientHttpRequestFactory(httpClient))
                        .build();

        ApiClient apiClient =
                new ApiClient(restClient);

        apiClient.setBasePath(baseUrl);

        return apiClient;
    }

    @Bean
    public MonthlySummaryApi monthlySummaryApi(
            ApiClient summaryApiClient) {

        return new MonthlySummaryApi(summaryApiClient);
    }
}
