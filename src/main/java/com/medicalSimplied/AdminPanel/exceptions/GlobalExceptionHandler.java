package com.medicalSimplied.AdminPanel.exceptions;

import com.medicalSimplied.AdminPanel.util.GlobalResponse;
import com.medicalSimplied.AdminPanel.util.ResponseBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handle any custom runtime exception (like invalid password, inactive admin, etc.)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<GlobalResponse<Object>> handleRuntime(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ResponseBuilder.failure(
                        HttpStatus.UNAUTHORIZED,
                        "Login failed",
                        "/api/admin/login",
                        ex.getMessage()
                ));
    }

    // Optional: handle all other unexpected errors
    @ExceptionHandler(Exception.class)
    public ResponseEntity<GlobalResponse<Object>> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseBuilder.failure(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Unexpected error",
                        "/api/admin/login",
                        ex.getMessage()
                ));
    }
}
