package com.fintech.statementprocessor.application.port.out;

import com.fintech.statementprocessor.domain.model.MonthlySummary;

public interface MonthlySummaryPort {

    void submitSummary(MonthlySummary summary, String requestId);
}
