package com.loanmanagement.emi.infrastructure.exception;

import com.loanmanagement.common.exception.BusinessException;
import com.loanmanagement.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.reflect.Method;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleResourceNotFoundException_success() {
        ResourceNotFoundException ex = new ResourceNotFoundException("EMI not found");

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                handler.handleResourceNotFoundException(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Not Found", response.getBody().getError());
        assertEquals("EMI not found", response.getBody().getMessage());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void handleBusinessException_success() {
        BusinessException ex = new BusinessException("Invalid payment");

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                handler.handleBusinessException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Bad Request", response.getBody().getError());
        assertEquals("Invalid payment", response.getBody().getMessage());
    }

    @Test
    void handleIllegalArgumentException_success() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid argument");

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                handler.handleIllegalArgumentException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Bad Request", response.getBody().getError());
        assertEquals("Invalid argument", response.getBody().getMessage());
    }

    @Test
    void handleGlobalException_success() {
        Exception ex = new RuntimeException("Unexpected");

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                handler.handleGlobalException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Internal Server Error", response.getBody().getError());
        assertEquals(
                "An unexpected error occurred. Please try again later.",
                response.getBody().getMessage()
        );
    }

    @Test
    void handleValidationExceptions_success() throws Exception {
        Object target = new Object();
        BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult(target, "request");

        bindingResult.addError(new FieldError(
                "request",
                "amount",
                "Amount must be greater than zero"
        ));

        Method method = this.getClass().getDeclaredMethod("dummyMethod", String.class);
        MethodParameter methodParameter = new MethodParameter(method, 0);

        MethodArgumentNotValidException ex =
                new MethodArgumentNotValidException(methodParameter, bindingResult);

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                handler.handleValidationExceptions(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Validation Failed", response.getBody().getError());
        assertEquals("Invalid request parameters", response.getBody().getMessage());

        Map<String, String> errors = response.getBody().getValidationErrors();
        assertNotNull(errors);
        assertEquals("Amount must be greater than zero", errors.get("amount"));
    }

    // Dummy method used only for MethodParameter construction via reflection
    @SuppressWarnings("unused")
    private void dummyMethod(String arg) {
    }
}
