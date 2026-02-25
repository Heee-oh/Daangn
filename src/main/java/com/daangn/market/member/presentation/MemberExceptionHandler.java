package com.daangn.market.member.presentation;

import com.daangn.market.member.domain.exception.memberRegion.MemberRegionNotFoundException;
import com.daangn.market.member.domain.exception.memberRegion.MemberRegionVerificationFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice(basePackages = "com.daangn.market.member")
public class MemberExceptionHandler {

    @ExceptionHandler(MemberRegionNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleRegionNotFound(MemberRegionNotFoundException e) {
        return error(HttpStatus.NOT_FOUND, "MEMBER_REGION_NOT_FOUND", e.getMessage());
    }

    @ExceptionHandler(MemberRegionVerificationFailedException.class)
    public ResponseEntity<ApiErrorResponse> handleRegionVerificationFailed(MemberRegionVerificationFailedException e) {
        return error(HttpStatus.BAD_REQUEST, "MEMBER_REGION_VERIFICATION_FAILED", e.getMessage());
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
