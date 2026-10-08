package com.fintech.statementprocessor.integration;

import com.fintech.statementprocessor.application.port.out.MonthlySummaryPort;
import com.fintech.statementprocessor.configuration.SummaryApiTestConfiguration;
import com.fintech.statementprocessor.domain.model.MonthlySummary;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;


import java.math.BigDecimal;
import java.time.YearMonth;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
@ActiveProfiles("test")
@Import(SummaryApiTestConfiguration.class)
class MonthlySummaryApiAdapterIntegrationTest {

    private static final WireMockServer wireMock =
            new WireMockServer(0);

    static {
        wireMock.start();
    }

    @AfterAll
    static void stopWireMock() {
        wireMock.stop();
    }

    @BeforeEach
    void resetWireMock() {
        wireMock.resetAll();
    }

    @DynamicPropertySource
    static void overrideProperties(
            DynamicPropertyRegistry registry) {

        registry.add(
                "summary.api.base-url",
                wireMock::baseUrl);
    }

    @Autowired
    private MonthlySummaryPort monthlySummaryPort;

    @Test
    void shouldSubmitMonthlySummary() {

        wireMock.stubFor(
                post(urlPathEqualTo("/monthly-summaries"))
                        .willReturn(
                                aResponse()
                                        .withStatus(201)
                        )
        );

        MonthlySummary summary =
                new MonthlySummary(
                        "account-12345",
                        YearMonth.of(2026, 10),
                        "EUR",
                        BigDecimal.valueOf(1000),
                        BigDecimal.valueOf(250),
                        BigDecimal.valueOf(750)
                );

        assertDoesNotThrow(
                () -> monthlySummaryPort.submitSummary(summary)
        );

        wireMock.verify(
                1,
                postRequestedFor(
                        urlPathEqualTo("/monthly-summaries"))
                        .withHeader(
                                "Idempotency-Key",
                                matching(".+"))
                        .withHeader(
                                "X-Correlation-ID",
                                matching(".+"))
                        .withRequestBody(
                                equalToJson("""
                            {
                              "accountId": "account-12345",
                              "month": "2026-10",
                              "currency": "EUR",
                              "income": 1000,
                              "spending": 250,
                              "balance": 750
                            }
                            """)
                        )
        );

        wireMock.verify(
                1,
                postRequestedFor(
                        urlPathEqualTo("/monthly-summaries"))
                        .withHeader(
                                "Content-Type",
                                containing("application/json"))
        );
    }
}
