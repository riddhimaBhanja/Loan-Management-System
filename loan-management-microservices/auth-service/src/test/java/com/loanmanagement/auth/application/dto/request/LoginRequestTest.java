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
    @Test
    void equalsAndHashCode_shouldWorkCorrectly() {
        LoginRequest r1 = LoginRequest.builder()
                .username("user")
                .password("pass")
                .build();

        LoginRequest r2 = LoginRequest.builder()
                .username("user")
                .password("pass")
                .build();

        LoginRequest r3 = LoginRequest.builder()
                .username("other")
                .password("pass")
                .build();

        assertThat(r1).isEqualTo(r2);
        assertThat(r1.hashCode()).isEqualTo(r2.hashCode());
        assertThat(r1).isNotEqualTo(r3);
    }

    @Test
    void equals_shouldReturnFalseForNullAndDifferentType() {
        LoginRequest request = LoginRequest.builder()
                .username("user")
                .password("pass")
                .build();

        assertThat(request).isNotEqualTo(null);
        assertThat(request).isNotEqualTo("not-login-request");
    }

    @Test
    void toString_shouldContainClassNameAndFields() {
        LoginRequest request = LoginRequest.builder()
                .username("user")
                .password("pass")
                .build();

        String value = request.toString();

        assertThat(value).isNotNull();
        assertThat(value).contains("LoginRequest");
        assertThat(value).contains("user");
    }

    @Test
    void constructors_shouldCreateObjectsSuccessfully() {
        LoginRequest noArgs = new LoginRequest();
        assertThat(noArgs).isNotNull();

        LoginRequest allArgs = new LoginRequest("user", "pass");

        assertThat(allArgs.getUsername()).isEqualTo("user");
        assertThat(allArgs.getPassword()).isEqualTo("pass");
    }

}
