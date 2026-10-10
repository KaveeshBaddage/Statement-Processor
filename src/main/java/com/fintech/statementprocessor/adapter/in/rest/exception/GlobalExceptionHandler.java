package com.fintech.statementprocessor.adapter.in.rest.exception;

import com.fintech.statementprocessor.adapter.out.exception.SummaryApiException;
import com.fintech.statementprocessor.adapter.out.exception.SummaryApiUnavailableException;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex) {

        if ("month".equals(ex.getName())) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(
                            "INVALID_MONTH",
                            "Month must be in yyyy-MM format (e.g. 2026-10)"
                    ));
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        "INVALID_REQUEST",
                        "Invalid request parameter"
                ));
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAccountNotFound(
            AccountNotFoundException ex) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("400", ex.getMessage()));
    }

    @ExceptionHandler(BankStatementApiUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleApiUnavailable(
            BankStatementApiUnavailableException ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(new ErrorResponse(
                        "502",
                        "Bank statement provider is currently unavailable"));
    }

    @ExceptionHandler(SummaryApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(
            SummaryApiException ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(new ErrorResponse(
                        "402",
                        "Summery API returns"));
    }

    @ExceptionHandler(SummaryApiUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleApiUnavailable(
            SummaryApiUnavailableException ex) {

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ErrorResponse(
                        "503",
                        "Summery API is currently unavailable"));
    }

    @ExceptionHandler(RequestNotPermitted.class)
    public ResponseEntity<ErrorResponse> handleRateLimitExceeded(
            RequestNotPermitted ex) {

        return ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .body(new ErrorResponse(
                        "429",
                         "Too many requests. Please try again later."
                ));
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<ErrorResponse> handleResourceAccessException(
            ResourceAccessException  ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(new ErrorResponse(
                        "502",
                        "A downstream service is temporarily unavailable. Please try again later"
                ));
    }
}
