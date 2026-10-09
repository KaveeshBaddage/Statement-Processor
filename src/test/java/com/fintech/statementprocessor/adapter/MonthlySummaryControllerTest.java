package com.fintech.statementprocessor.adapter;


import com.fintech.statementprocessor.adapter.in.rest.controller.StatementProcessingController;
import com.fintech.statementprocessor.adapter.in.rest.exception.AccountNotFoundException;
import com.fintech.statementprocessor.adapter.in.rest.exception.BankStatementApiUnavailableException;
import com.fintech.statementprocessor.application.port.in.MonthlySummaryResult;
import com.fintech.statementprocessor.application.port.in.ProcessMonthlyStatementUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.YearMonth;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StatementProcessingController.class)
public class MonthlySummaryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProcessMonthlyStatementUseCase useCase;


    @Test
    void shouldProcessMonthlySummary() throws Exception {

        MonthlySummaryResult summary = new MonthlySummaryResult(
                "account-12345",
                YearMonth.of(2026, 10),
                true
        );

        when(useCase.process(
                "account-12345",
                YearMonth.of(2026, 10),
                "test-request-123"
        )).thenReturn(summary);

        mockMvc.perform(post("/api/v1/accounts/account-12345/monthly-summary/2026-10")
                        .header("X-Request-ID", "test-request-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value("account-12345"))
                .andExpect(jsonPath("$.month").value("2026-10"))
                .andExpect(jsonPath("$.status").value(true));
    }


    @Test
    void shouldReturnBadRequestWhenMonthIsInvalid() throws Exception {


        mockMvc.perform(post("/api/v1/accounts/account-12345/monthly-summary/2026-13"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.code").value("INVALID_MONTH"))
                .andExpect(jsonPath("$.message").value("Month must be in yyyy-MM format (e.g. 2026-10)"));
    }

    @Test
    void shouldReturnNotFoundWhenAccountDoesNotExist() throws Exception {

        when(useCase.process(
                "non-account-12345",
                YearMonth.of(2026, 10),
                "test-request-123"
        )).thenThrow(new AccountNotFoundException("non-account-12345"));

        mockMvc.perform(post("/api/v1/accounts/non-account-12345/monthly-summary/2026-10")
                        .header("X-Request-ID", "test-request-123"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnBadGatewayWhenBankStatementApiUnavailable() throws Exception {

        when(useCase.process(
                "account-123",
                YearMonth.of(2026, 11),
                "test-request-123"
        )).thenThrow(new BankStatementApiUnavailableException("Hypo Bank API is unavailable", null));

        mockMvc.perform(post("/api/v1/accounts/account-123/monthly-summary/2026-11")
                        .header("X-Request-ID", "test-request-123"))
                .andExpect(status().is5xxServerError());
    }
}
