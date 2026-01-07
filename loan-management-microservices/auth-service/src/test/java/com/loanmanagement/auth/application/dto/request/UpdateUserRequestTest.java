package com.loanmanagement.auth.application.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UpdateUserRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldPassValidationForValidRequest() {
        UpdateUserRequest request = UpdateUserRequest.builder()
                .email("user@example.com")
                .fullName("John Doe")
                .phoneNumber("9876543210")
                .password("securePass123")
                .build();

        Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailForInvalidEmail() {
        UpdateUserRequest request = UpdateUserRequest.builder()
                .email("invalid-email")
                .build();

        Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
    }

    @Test
    void shouldFailWhenEmailExceedsMaxLength() {
        // Create a valid email format that exceeds 100 characters
        // Local part (50 chars) + @ + domain (55 chars) = 106 characters total
        // This only violates @Size(max=100), not @Email (local part < 64 chars)
        UpdateUserRequest request = UpdateUserRequest.builder()
                .email("a".repeat(50) + "@" + "b".repeat(51) + ".com")
                .build();

        Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
    }

    @Test
    void shouldFailWhenFullNameExceedsMaxLength() {
        UpdateUserRequest request = UpdateUserRequest.builder()
                .fullName("a".repeat(101))
                .build();

        Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
    }

    @Test
    void shouldFailWhenPasswordTooShort() {
        UpdateUserRequest request = UpdateUserRequest.builder()
                .password("123")
                .build();

        Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
    }

    @Test
    void shouldFailWhenPasswordTooLong() {
        UpdateUserRequest request = UpdateUserRequest.builder()
                .password("a".repeat(101))
                .build();

        Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
    }

    @Test
    void shouldAllowNullFields() {
        UpdateUserRequest request = new UpdateUserRequest();

        Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }
}