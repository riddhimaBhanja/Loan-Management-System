package com.loanmanagement.auth.infrastructure.exception;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseTest {

    @Test
    void defaultConstructor_shouldSetDefaults() {
        ErrorResponse errorResponse = new ErrorResponse();

        assertFalse(errorResponse.getSuccess());
        assertNull(errorResponse.getError());
        assertNotNull(errorResponse.getTimestamp());
    }

    @Test
    void allArgsConstructor_shouldSetAllFields() {
        ErrorResponse.ErrorDetails errorDetails =
                ErrorResponse.ErrorDetails.builder()
                        .code("ERROR_CODE")
                        .message("Error message")
                        .details("Error details")
                        .build();

        LocalDateTime now = LocalDateTime.now();

        ErrorResponse errorResponse =
                new ErrorResponse(false, errorDetails, now);

        assertFalse(errorResponse.getSuccess());
        assertEquals(errorDetails, errorResponse.getError());
        assertEquals(now, errorResponse.getTimestamp());
    }

    @Test
    void of_shouldCreateErrorResponseWithCodeAndMessage() {
        ErrorResponse errorResponse = ErrorResponse.of("NOT_FOUND", "Resource not found");

        assertFalse(errorResponse.getSuccess());
        assertNotNull(errorResponse.getTimestamp());
        assertNotNull(errorResponse.getError());
        assertEquals("NOT_FOUND", errorResponse.getError().getCode());
        assertEquals("Resource not found", errorResponse.getError().getMessage());
        assertNull(errorResponse.getError().getDetails());
        assertNull(errorResponse.getError().getFields());
    }

    @Test
    void of_shouldCreateErrorResponseWithCodeMessageAndDetails() {
        ErrorResponse errorResponse =
                ErrorResponse.of("BAD_REQUEST", "Invalid input", "Field value is invalid");

        assertFalse(errorResponse.getSuccess());
        assertNotNull(errorResponse.getTimestamp());
        assertNotNull(errorResponse.getError());
        assertEquals("BAD_REQUEST", errorResponse.getError().getCode());
        assertEquals("Invalid input", errorResponse.getError().getMessage());
        assertEquals("Field value is invalid", errorResponse.getError().getDetails());
    }

    @Test
    void ofValidation_shouldCreateValidationErrorResponse() {
        Map<String, String> fields = new HashMap<>();
        fields.put("email", "Email is required");
        fields.put("password", "Password is too short");

        ErrorResponse errorResponse =
                ErrorResponse.ofValidation("Validation failed", fields);

        assertFalse(errorResponse.getSuccess());
        assertNotNull(errorResponse.getTimestamp());
        assertNotNull(errorResponse.getError());
        assertEquals("VALIDATION_ERROR", errorResponse.getError().getCode());
        assertEquals("Validation failed", errorResponse.getError().getMessage());
        assertEquals(fields, errorResponse.getError().getFields());
        assertNull(errorResponse.getError().getDetails());
    }

    @Test
    void errorDetailsBuilder_shouldWorkCorrectly() {
        Map<String, String> fields = new HashMap<>();
        fields.put("username", "Already exists");

        ErrorResponse.ErrorDetails errorDetails =
                ErrorResponse.ErrorDetails.builder()
                        .code("DUPLICATE")
                        .message("Duplicate value")
                        .details("Username already taken")
                        .fields(fields)
                        .build();

        assertEquals("DUPLICATE", errorDetails.getCode());
        assertEquals("Duplicate value", errorDetails.getMessage());
        assertEquals("Username already taken", errorDetails.getDetails());
        assertEquals(fields, errorDetails.getFields());
    }
}
