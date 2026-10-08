package com.fintech.statementprocessor.integration;

import com.fintech.statementprocessor.adapter.in.rest.exception.AccountNotFoundException;
import com.fintech.statementprocessor.adapter.in.rest.exception.BankStatementApiUnavailableException;
import com.fintech.statementprocessor.application.port.out.BankStatementPort;
import com.fintech.statementprocessor.domain.model.TransactionPage;
import com.github.tomakehurst.wiremock.WireMockServer;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.time.YearMonth;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class BankStatementApiAdapterIntegrationTest {

    private static final WireMockServer wireMock = new WireMockServer(0);

    @Autowired
    private BankStatementPort bankStatementPort;

    @BeforeAll
    static void startWireMock() {
        wireMock.start();
    }

    @AfterAll
    static void stopWireMock() {
        wireMock.stop();
    }

    @BeforeEach
    void reset() {
        wireMock.resetAll();
    }

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add(
                "bank.api.base-url",
                wireMock::baseUrl
        );
    }

    @Test
    void shouldRetrieveTransactions() {

        wireMock.stubFor(
                get(urlPathEqualTo(
                        "/accounts/account-12345/statements/2026-10"))
                        .withQueryParam("limit", equalTo("100"))
                        .willReturn(
                                okJson("""
                                        {
                                          "accountId": "account-12345",
                                          "month": "2026-10",
                                          "currency": "EUR",
                                          "transactions": [
                                            {
                                              "transactionId": "txn-001",
                                              "bookingDate": "2026-10-02",
                                              "amount": 1000,
                                              "currency": "EUR",
                                              "direction": "CREDIT",
                                              "category": "SALARY",
                                              "description": "Salary"
                                            }
                                          ],
                                          "nextCursor": null,
                                          "hasMore": false
                                        }
                                        """)));

        TransactionPage page =
                bankStatementPort.getTransactions(
                        "account-12345",
                        YearMonth.of(2026, 10),
                        100,
                        null);

        assertEquals("EUR", page.currency());
        assertEquals(1, page.transactions().size());
    }

    @Test
    void shouldThrowAccountNotFoundException() {

        wireMock.stubFor(
                get(urlPathEqualTo(
                        "/accounts/non-account-12345/statements/2026-10"))
                        .willReturn(
                                aResponse()
                                        .withStatus(404)
                                        .withHeader(
                                                "Content-Type",
                                                "application/json")));

        assertThrows(
                AccountNotFoundException.class,
                () -> bankStatementPort.getTransactions(
                        "non-account-12345",
                        YearMonth.of(2026, 10),
                        100,
                        null));
    }

    @Test
    void shouldThrowBankStatementApiUnavailableException() {

        wireMock.stubFor(
                get(urlPathEqualTo(
                        "/accounts/account-12345/statements/2026-10"))
                        .willReturn(
                                aResponse()
                                        .withStatus(500)));

        assertThrows(
                BankStatementApiUnavailableException.class,
                () -> bankStatementPort.getTransactions(
                        "account-12345",
                        YearMonth.of(2026, 10),
                        100,
                        null));
    }

    @Test
    void shouldPassCursorToBankApi() {

        wireMock.stubFor(
                get(urlPathEqualTo(
                        "/accounts/account-12345/statements/2026-10"))
                        .withQueryParam("limit", equalTo("100"))
                        .withQueryParam("cursor", equalTo("cursor-2"))
                        .willReturn(
                                okJson("""
                            {
                              "accountId": "account-12345",
                              "month": "2026-10",
                              "currency": "EUR",
                              "transactions": [],
                              "nextCursor": null,
                              "hasMore": false
                            }
                            """)));

        bankStatementPort.getTransactions(
                "account-12345",
                YearMonth.of(2026, 10),
                100,
                "cursor-2");

        wireMock.verify(
                1,
                getRequestedFor(
                        urlPathEqualTo("/accounts/account-12345/statements/2026-10"))
                        .withQueryParam("limit", equalTo("100"))
                        .withQueryParam("cursor", equalTo("cursor-2")));
    }

    @Test
    void shouldPassCursorToBankApiAndMapNextCursor() {

        wireMock.stubFor(
                get(urlPathEqualTo(
                        "/accounts/account-12345/statements/2026-10"))
                        .withQueryParam("limit", equalTo("100"))
                        .withQueryParam("cursor", equalTo("cursor-2"))
                        .willReturn(
                                okJson("""
                            {
                              "accountId": "account-12345",
                              "month": "2026-10",
                              "currency": "EUR",
                              "transactions": [],
                              "nextCursor": "cursor-3",
                              "hasMore": true
                            }
                            """)));

        TransactionPage page =
                bankStatementPort.getTransactions(
                        "account-12345",
                        YearMonth.of(2026, 10),
                        100,
                        "cursor-2");

        assertEquals("cursor-3", page.nextCursor());

        wireMock.verify(
                getRequestedFor(
                        urlPathEqualTo(
                                "/accounts/account-12345/statements/2026-10"))
                        .withQueryParam("cursor", equalTo("cursor-2")));
    }
}
