package com.loanmanagement.common.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private Map<String, String> validationErrors;

    public static ErrorResponse of(int status, String error, String message, String path) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status)
                .error(error)
                .message(message)
                .path(path)
                .build();
    }

    public static ErrorResponse of(String error, String message) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .error(error)
                .message(message)
                .build();
    }

    public static ErrorResponse of(String error, String message, String path) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .error(error)
                .message(message)
                .path(path)
                .build();
    }

    public static ErrorResponse ofValidation(String message, Map<String, String> validationErrors) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .error("Validation Failed")
                .message(message)
                .validationErrors(validationErrors)
                .build();
    }
}
