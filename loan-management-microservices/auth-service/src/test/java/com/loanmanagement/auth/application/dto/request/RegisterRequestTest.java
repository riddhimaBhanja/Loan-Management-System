package com.loanmanagement.auth.application.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RegisterRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validRegisterRequest_shouldHaveNoViolations() {
        RegisterRequest request = RegisterRequest.builder()
                .username("test.user_01")
                .email("test@example.com")
                .password("password123")
                .fullName("Test User")
                .phoneNumber("+919876543210")
                .build();

        Set<ConstraintViolation<RegisterRequest>> violations =
                validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void invalidUsername_shouldFailValidation() {
        RegisterRequest request = RegisterRequest.builder()
                .username("invalid user!")
                .email("test@example.com")
                .password("password123")
                .fullName("Test User")
                .phoneNumber("+919876543210")
                .build();

        assertFalse(validator.validate(request).isEmpty());
    }

    @Test
    void invalidEmail_shouldFailValidation() {
        RegisterRequest request = RegisterRequest.builder()
                .username("testuser")
                .email("invalid-email")
                .password("password123")
                .fullName("Test User")
                .phoneNumber("+919876543210")
                .build();

        assertFalse(validator.validate(request).isEmpty());
    }

    @Test
    void shortPassword_shouldFailValidation() {
        RegisterRequest request = RegisterRequest.builder()
                .username("testuser")
                .email("test@example.com")
                .password("123")
                .fullName("Test User")
                .phoneNumber("+919876543210")
                .build();

        assertFalse(validator.validate(request).isEmpty());
    }

    @Test
    void invalidPhoneNumber_shouldFailValidation() {
        RegisterRequest request = RegisterRequest.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .fullName("Test User")
                .phoneNumber("abc123")
                .build();

        assertFalse(validator.validate(request).isEmpty());
    }

    @Test
    void blankFields_shouldFailValidation() {
        RegisterRequest request = new RegisterRequest();

        assertFalse(validator.validate(request).isEmpty());
    }
    @Test
    void equalsAndHashCode_shouldWorkCorrectly() {
        RegisterRequest req1 = RegisterRequest.builder()
                .username("user1")
                .email("user1@test.com")
                .password("password123")
                .fullName("User One")
                .phoneNumber("9876543210")
                .build();

        RegisterRequest req2 = RegisterRequest.builder()
                .username("user1")
                .email("user1@test.com")
                .password("password123")
                .fullName("User One")
                .phoneNumber("9876543210")
                .build();

        RegisterRequest req3 = RegisterRequest.builder()
                .username("user2")
                .email("user2@test.com")
                .password("password123")
                .fullName("User Two")
                .phoneNumber("9876543211")
                .build();

        assertEquals(req1, req2);
        assertEquals(req1.hashCode(), req2.hashCode());
        assertNotEquals(req1, req3);
    }

    @Test
    void equals_shouldReturnFalseForNullAndDifferentType() {
        RegisterRequest request = RegisterRequest.builder()
                .username("user")
                .email("user@test.com")
                .password("password123")
                .fullName("User")
                .phoneNumber("9876543210")
                .build();

        assertNotEquals(request, null);
        assertNotEquals(request, "some-string");
    }

    @Test
    void toString_shouldContainClassNameAndFields() {
        RegisterRequest request = RegisterRequest.builder()
                .username("user")
                .email("user@test.com")
                .password("password123")
                .fullName("User")
                .phoneNumber("9876543210")
                .build();

        String result = request.toString();

        assertNotNull(result);
        assertTrue(result.contains("RegisterRequest"));
        assertTrue(result.contains("user"));
        assertTrue(result.contains("user@test.com"));
    }

    @Test
    void constructors_shouldCreateObjectsSuccessfully() {
        RegisterRequest noArgs = new RegisterRequest();
        assertNotNull(noArgs);

        RegisterRequest allArgs = new RegisterRequest(
                "user",
                "user@test.com",
                "password123",
                "User",
                "9876543210"
        );

        assertEquals("user", allArgs.getUsername());
        assertEquals("user@test.com", allArgs.getEmail());
        assertEquals("password123", allArgs.getPassword());
        assertEquals("User", allArgs.getFullName());
        assertEquals("9876543210", allArgs.getPhoneNumber());
    }

}
