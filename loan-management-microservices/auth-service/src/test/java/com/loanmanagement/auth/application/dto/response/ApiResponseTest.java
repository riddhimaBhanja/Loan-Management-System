package com.loanmanagement.auth.application.dto.response;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ApiResponseTest {

    @Test
    void shouldCreateSuccessResponseWithData() {
        ApiResponse<String> response = ApiResponse.success("Success message", "DATA");

        assertTrue(response.getSuccess());
        assertEquals("DATA", response.getData());
        assertEquals("Success message", response.getMessage());
        assertNotNull(response.getTimestamp());
    }

    @Test
    void shouldCreateSuccessResponseWithMessageAndData() {
        ApiResponse<String> response = ApiResponse.success("Operation successful", "DATA");

        assertTrue(response.getSuccess());
        assertEquals("Operation successful", response.getMessage());
        assertEquals("DATA", response.getData());
        assertNotNull(response.getTimestamp());
    }

    @Test
    void shouldCreateSuccessResponseWithOnlyMessage() {
        ApiResponse<Void> response = ApiResponse.success("Operation successful");

        assertTrue(response.getSuccess());
        assertEquals("Operation successful", response.getMessage());
        assertNull(response.getData());
        assertNotNull(response.getTimestamp());
    }

    @Test
    void shouldCreateErrorResponseWithMessage() {
        ApiResponse<Void> response = ApiResponse.error("Something went wrong");

        assertFalse(response.getSuccess());
        assertEquals("Something went wrong", response.getMessage());
        assertNull(response.getData());
        assertNotNull(response.getTimestamp());
    }

    @Test
    void shouldCreateErrorResponseWithMessageAndData() {
        ApiResponse<String> response = ApiResponse.error("Error occurred", "ERROR_DATA");

        assertFalse(response.getSuccess());
        assertEquals("Error occurred", response.getMessage());
        assertEquals("ERROR_DATA", response.getData());
        assertNotNull(response.getTimestamp());
    }

    @Test
    void shouldUseBuilderDefaults() {
        ApiResponse<String> response = ApiResponse.<String>builder().build();

        assertTrue(response.getSuccess());
        assertNull(response.getMessage());
        assertNull(response.getData());
        assertNotNull(response.getTimestamp());
    }

    @Test
    void timestampShouldBeRecent() {
        LocalDateTime before = LocalDateTime.now();
        ApiResponse<String> response = ApiResponse.success("DATA");
        LocalDateTime after = LocalDateTime.now();

        assertTrue(
                !response.getTimestamp().isBefore(before) &&
                !response.getTimestamp().isAfter(after)
        );
    }
}
