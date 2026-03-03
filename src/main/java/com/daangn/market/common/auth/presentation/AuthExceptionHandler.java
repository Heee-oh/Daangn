package com.daangn.market.common.auth.presentation;

import com.daangn.market.common.auth.exception.AuthBadRequestException;
import com.daangn.market.common.auth.exception.AuthConflictException;
import com.daangn.market.common.auth.exception.AuthUnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice(basePackages = "com.daangn.market.common.auth")
public class AuthExceptionHandler {

    @ExceptionHandler(AuthBadRequestException.class)
    public ResponseEntity<ApiErrorResponse> handleBadRequest(AuthBadRequestException e) {
        return error(HttpStatus.BAD_REQUEST, "AUTH_BAD_REQUEST", e.getMessage());
    }

    @ExceptionHandler(AuthConflictException.class)
    public ResponseEntity<ApiErrorResponse> handleConflict(AuthConflictException e) {
        return error(HttpStatus.CONFLICT, "AUTH_CONFLICT", e.getMessage());
    }

    @ExceptionHandler(AuthUnauthorizedException.class)
    public ResponseEntity<ApiErrorResponse> handleUnauthorized(AuthUnauthorizedException e) {
        return error(HttpStatus.UNAUTHORIZED, "AUTH_UNAUTHORIZED", e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(fieldError -> fieldError.getField() + " " + fieldError.getDefaultMessage())
                .orElse("Invalid request");
        return error(HttpStatus.BAD_REQUEST, "VALIDATION_FAILED", message);
    }

    private ResponseEntity<ApiErrorResponse> error(HttpStatus status, String code, String message) {
        return ResponseEntity.status(status)
                .body(new ApiErrorResponse(code, message, Instant.now()));
    }

    public record ApiErrorResponse(String code, String message, Instant timestamp) {}
}
