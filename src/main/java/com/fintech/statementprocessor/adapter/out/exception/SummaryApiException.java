package com.fintech.statementprocessor.adapter.out.exception;

public class SummaryApiException extends RuntimeException {

    public SummaryApiException(String message) {
        super(message);
    }

    public SummaryApiException(String message, Throwable cause) {
        super(message, cause);
    }
}