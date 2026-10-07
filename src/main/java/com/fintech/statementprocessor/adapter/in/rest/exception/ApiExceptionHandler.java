package com.fintech.statementprocessor.adapter.in.rest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ProblemResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex) {

        if ("month".equals(ex.getName())) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ProblemResponse(
                            "INVALID_MONTH",
                            "Month must be in yyyy-MM format (e.g. 2026-10)"
                    ));
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ProblemResponse(
                        "INVALID_REQUEST",
                        "Invalid request parameter"
                ));
    }
}
