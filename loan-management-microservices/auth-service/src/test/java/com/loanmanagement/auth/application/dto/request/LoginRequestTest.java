package com.loanmanagement.auth.application.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class LoginRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldCreateValidLoginRequest() {
        LoginRequest request = LoginRequest.builder()
                .username("testuser")
                .password("password123")
                .build();

        Set<ConstraintViolation<LoginRequest>> violations =
                validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void shouldFailValidationWhenUsernameIsBlank() {
        LoginRequest request = LoginRequest.builder()
                .username("")
                .password("password123")
                .build();

        Set<ConstraintViolation<LoginRequest>> violations =
                validator.validate(request);

        assertThat(violations).isNotEmpty();
    }

    @Test
    void shouldFailValidationWhenPasswordIsBlank() {
        LoginRequest request = LoginRequest.builder()
                .username("testuser")
                .password("")
                .build();

        Set<ConstraintViolation<LoginRequest>> violations =
                validator.validate(request);

        assertThat(violations).isNotEmpty();
    }
}
