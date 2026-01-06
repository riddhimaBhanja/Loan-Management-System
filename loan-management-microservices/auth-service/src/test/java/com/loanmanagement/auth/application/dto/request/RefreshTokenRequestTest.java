package com.loanmanagement.auth.application.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class RefreshTokenRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldCreateValidRefreshTokenRequest() {
        RefreshTokenRequest request = RefreshTokenRequest.builder()
                .refreshToken("valid-refresh-token")
                .build();

        Set<ConstraintViolation<RefreshTokenRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldFailValidationWhenRefreshTokenBlank() {
        RefreshTokenRequest request = RefreshTokenRequest.builder()
                .refreshToken("")
                .build();

        Set<ConstraintViolation<RefreshTokenRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
    }
}
