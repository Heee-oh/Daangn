package com.daangn.market.Listing.presentation;

import com.daangn.market.Listing.exception.ListingAccessDeniedException;
import com.daangn.market.Listing.exception.ListingBadRequestException;
import com.daangn.market.Listing.exception.ListingConflictException;
import com.daangn.market.Listing.exception.ListingNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice(basePackages = "com.daangn.market.Listing")
public class ListingExceptionHandler {

    @ExceptionHandler(ListingNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(ListingNotFoundException e) {
        return error(HttpStatus.NOT_FOUND, "LISTING_NOT_FOUND", e.getMessage());
    }

    @ExceptionHandler(ListingAccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDenied(ListingAccessDeniedException e) {
        return error(HttpStatus.FORBIDDEN, "LISTING_ACCESS_DENIED", e.getMessage());
    }

    @ExceptionHandler(ListingBadRequestException.class)
    public ResponseEntity<ApiErrorResponse> handleBadRequest(ListingBadRequestException e) {
        return error(HttpStatus.BAD_REQUEST, "LISTING_BAD_REQUEST", e.getMessage());
    }

    @ExceptionHandler(ListingConflictException.class)
    public ResponseEntity<ApiErrorResponse> handleConflict(ListingConflictException e) {
        return error(HttpStatus.CONFLICT, "LISTING_CONFLICT", e.getMessage());
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
}
