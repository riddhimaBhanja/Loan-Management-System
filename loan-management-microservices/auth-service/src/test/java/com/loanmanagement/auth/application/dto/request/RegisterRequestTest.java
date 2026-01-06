package com.loanmanagement.auth.application.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("RegisterRequest Validation Tests")
class RegisterRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should pass validation when all fields are valid")
    void shouldPassValidation_WhenAllFieldsValid() {
        RegisterRequest request = RegisterRequest.builder()
                .username("valid_user-123")
                .email("user@example.com")
                .password("password123")
                .fullName("Valid User")
                .phoneNumber("+91-9876543210")
                .build();

        Set<ConstraintViolation<RegisterRequest>> violations =
                validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should fail validation when username is blank")
    void shouldFailValidation_WhenUsernameBlank() {
        RegisterRequest request = RegisterRequest.builder()
                .username("")
                .email("user@example.com")
                .password("password123")
                .fullName("Valid User")
                .phoneNumber("9876543210")
                .build();

        Set<ConstraintViolation<RegisterRequest>> violations =
                validator.validate(request);

        assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("Should fail validation when username pattern is invalid")
    void shouldFailValidation_WhenUsernamePatternInvalid() {
        RegisterRequest request = RegisterRequest.builder()
                .username("invalid@user")
                .email("user@example.com")
                .password("password123")
                .fullName("Valid User")
                .phoneNumber("9876543210")
                .build();

        Set<ConstraintViolation<RegisterRequest>> violations =
                validator.validate(request);

        assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("Should fail validation when email is invalid")
    void shouldFailValidation_WhenEmailInvalid() {
        RegisterRequest request = RegisterRequest.builder()
                .username("validuser")
                .email("invalid-email")
                .password("password123")
                .fullName("Valid User")
                .phoneNumber("9876543210")
                .build();

        Set<ConstraintViolation<RegisterRequest>> violations =
                validator.validate(request);

        assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("Should fail validation when password is too short")
    void shouldFailValidation_WhenPasswordTooShort() {
        RegisterRequest request = RegisterRequest.builder()
                .username("validuser")
                .email("user@example.com")
                .password("123")
                .fullName("Valid User")
                .phoneNumber("9876543210")
                .build();

        Set<ConstraintViolation<RegisterRequest>> violations =
                validator.validate(request);

        assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("Should fail validation when phone number is invalid")
    void shouldFailValidation_WhenPhoneNumberInvalid() {
        RegisterRequest request = RegisterRequest.builder()
                .username("validuser")
                .email("user@example.com")
                .password("password123")
                .fullName("Valid User")
                .phoneNumber("abc123")
                .build();

        Set<ConstraintViolation<RegisterRequest>> violations =
                validator.validate(request);

        assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("Should fail validation when all fields are null")
    void shouldFailValidation_WhenAllFieldsNull() {
        RegisterRequest request = new RegisterRequest();

        Set<ConstraintViolation<RegisterRequest>> violations =
                validator.validate(request);

        assertThat(violations).isNotEmpty();
    }
}
