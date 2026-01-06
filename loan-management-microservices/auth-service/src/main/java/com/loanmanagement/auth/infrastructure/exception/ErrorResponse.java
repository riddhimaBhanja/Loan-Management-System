package com.loanmanagement.auth.infrastructure.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Standard error response structure
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    @Builder.Default
    private Boolean success = false;

    private ErrorDetails error;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ErrorDetails {
        private String code;
        private String message;
        private String details;
        private Map<String, String> fields;
    }

    /**
     * Create error response with code and message
     */
    public static ErrorResponse of(String code, String message) {
        return ErrorResponse.builder()
                .success(false)
                .error(ErrorDetails.builder()
                        .code(code)
                        .message(message)
                        .build())
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Create error response with code, message and details
     */
    public static ErrorResponse of(String code, String message, String details) {
        return ErrorResponse.builder()
                .success(false)
                .error(ErrorDetails.builder()
                        .code(code)
                        .message(message)
                        .details(details)
                        .build())
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Create error response with validation errors
     */
    public static ErrorResponse ofValidation(String message, Map<String, String> fields) {
        return ErrorResponse.builder()
                .success(false)
                .error(ErrorDetails.builder()
                        .code("VALIDATION_ERROR")
                        .message(message)
                        .fields(fields)
                        .build())
                .timestamp(LocalDateTime.now())
                .build();
    }
}
