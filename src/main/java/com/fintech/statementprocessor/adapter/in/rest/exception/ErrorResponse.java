package com.fintech.statementprocessor.adapter.in.rest.exception;

public record ErrorResponse(
        String code,
        String message) {
}