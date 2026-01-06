package com.loanmanagement.auth.infrastructure.exception;

import com.loanmanagement.common.exception.BusinessException;
import com.loanmanagement.common.exception.ErrorResponse;
import com.loanmanagement.common.exception.ResourceNotFoundException;
import com.loanmanagement.common.exception.UnauthorizedException;
import com.loanmanagement.common.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.core.MethodParameter;

import java.lang.reflect.Method;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test class for GlobalExceptionHandler
 * Covers all handlers and branches to increase JaCoCo coverage
 */
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        webRequest = mock(WebRequest.class);
    }

    @Test
    void handleResourceNotFoundException() {
        ResponseEntity<ErrorResponse> response =
                handler.handleResourceNotFoundException(
                        new ResourceNotFoundException("Not found"),
                        webRequest
                );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void handleBusinessException_withErrorCode() {
        ResponseEntity<ErrorResponse> response =
                handler.handleBusinessException(
                        new BusinessException("Business error", "BUSINESS_ERROR"),
                        webRequest
                );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void handleBusinessException_withoutErrorCode() {
        ResponseEntity<ErrorResponse> response =
                handler.handleBusinessException(
                        new BusinessException("Business error"),
                        webRequest
                );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void handleMethodArgumentNotValidException() throws Exception {
        class DummyController {
            public void testMethod(String name) {}
        }

        Method method = DummyController.class.getMethod("testMethod", String.class);
        MethodParameter methodParameter = new MethodParameter(method, 0);

        BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult(new Object(), "object");

        bindingResult.addError(
                new FieldError("object", "name", "must not be null")
        );

        MethodArgumentNotValidException ex =
                new MethodArgumentNotValidException(methodParameter, bindingResult);

        ResponseEntity<ErrorResponse> response =
                handler.handleMethodArgumentNotValid(ex, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void handleUnauthorizedException() {
        ResponseEntity<ErrorResponse> response =
                handler.handleUnauthorizedException(
                        new UnauthorizedException("Unauthorized"),
                        webRequest
                );

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void handleAuthenticationException() {
        AuthenticationException ex =
                new BadCredentialsException("Bad credentials");

        ResponseEntity<ErrorResponse> response =
                handler.handleAuthenticationException(ex, webRequest);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void handleBadCredentialsException() {
        ResponseEntity<ErrorResponse> response =
                handler.handleBadCredentialsException(
                        new BadCredentialsException("Bad credentials"),
                        webRequest
                );

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void handleAccessDeniedException() {
        ResponseEntity<ErrorResponse> response =
                handler.handleAccessDeniedException(
                        new AccessDeniedException("Access denied"),
                        webRequest
                );

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void handleMethodArgumentTypeMismatch_withRequiredType() {
        MethodArgumentTypeMismatchException ex =
                new MethodArgumentTypeMismatchException(
                        "abc", Long.class, "id", null, new IllegalArgumentException()
                );

        ResponseEntity<ErrorResponse> response =
                handler.handleMethodArgumentTypeMismatch(ex, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void handleIllegalArgumentException() {
        ResponseEntity<ErrorResponse> response =
                handler.handleIllegalArgumentException(
                        new IllegalArgumentException("Illegal argument"),
                        webRequest
                );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void handleIllegalStateException() {
        ResponseEntity<ErrorResponse> response =
                handler.handleIllegalStateException(
                        new IllegalStateException("Illegal state"),
                        webRequest
                );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void handleDataIntegrityViolation_duplicateLoanType() {
        DataIntegrityViolationException ex =
                new DataIntegrityViolationException(
                        "Duplicate entry 'PL' for key 'idx_loan_type_name' in loan_types"
                );

        ResponseEntity<ErrorResponse> response =
                handler.handleDataIntegrityViolation(ex, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void handleDataIntegrityViolation_userRoles() {
        DataIntegrityViolationException ex =
                new DataIntegrityViolationException(
                        "Duplicate entry '1-2' for key 'user_roles'"
                );

        ResponseEntity<ErrorResponse> response =
                handler.handleDataIntegrityViolation(ex, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void handleDataIntegrityViolation_genericDuplicate() {
        DataIntegrityViolationException ex =
                new DataIntegrityViolationException("Duplicate entry");

        ResponseEntity<ErrorResponse> response =
                handler.handleDataIntegrityViolation(ex, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void handleGlobalException() {
        ResponseEntity<ErrorResponse> response =
                handler.handleGlobalException(
                        new RuntimeException("Unexpected"),
                        webRequest
                );

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
