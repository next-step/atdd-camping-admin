package com.camping.admin.controller;

import com.camping.admin.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    public GlobalExceptionHandler() {
        log.info("GlobalExceptionHandler initialized");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("IllegalArgumentException occurred: {}", e.getMessage());
        var errorResponse = new ErrorResponse(e.getMessage(), BAD_REQUEST.value());
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException e) {
        log.warn("IllegalStateException occurred: {}", e.getMessage());
        var errorResponse = new ErrorResponse(e.getMessage(), CONFLICT.value());
        return ResponseEntity.status(CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e) {
        log.error("Unexpected exception occurred: {}", e.getMessage(), e);
        var errorResponse = new ErrorResponse("Internal server error", INTERNAL_SERVER_ERROR.value());
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
