package com.fintech.statementprocessor.adapter.in.rest.exception;

public record ProblemResponse(
        String code,
        String message) {
}