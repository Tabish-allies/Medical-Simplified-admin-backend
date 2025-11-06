package com.medicalSimplied.AdminPanel.util;

import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.List;

/**
 * ✅ ResponseBuilder
 * Utility class for constructing GlobalResponse objects in a clean, reusable way.
 */
public class ResponseBuilder {

    private ResponseBuilder() {
        // Prevent instantiation
    }

    /**
     * Build a standard success response with HTTP 200.
     */
    public static <T> GlobalResponse<T> success(T data, String message, String path) {
        return GlobalResponse.<T>builder()
                .timestamp(Instant.now())
                .status(HttpStatus.OK.value())
                .success(true)
                .message(message)
                .path(path)
                .data(data)
                .build();
    }

    /**
     * Build a custom success response with specific HTTP status.
     */
    public static <T> GlobalResponse<T> success(HttpStatus status, T data, String message, String path) {
        return GlobalResponse.<T>builder()
                .timestamp(Instant.now())
                .status(status.value())
                .success(true)
                .message(message)
                .path(path)
                .data(data)
                .build();
    }

    /**
     * Build a failure response with a single error message.
     */
    public static <T> GlobalResponse<T> failure(HttpStatus status, String message, String path, String error) {
        return GlobalResponse.<T>builder()
                .timestamp(Instant.now())
                .status(status.value())
                .success(false)
                .message(message)
                .path(path)
                .errors(List.of(error))
                .build();
    }

    /**
     * Build a failure response with multiple error messages.
     */
    public static <T> GlobalResponse<T> failure(HttpStatus status, String message, String path, List<String> errors) {
        return GlobalResponse.<T>builder()
                .timestamp(Instant.now())
                .status(status.value())
                .success(false)
                .message(message)
                .path(path)
                .errors(errors)
                .build();
    }
}
