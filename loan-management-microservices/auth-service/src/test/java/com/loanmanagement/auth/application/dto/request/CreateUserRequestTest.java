package com.loanmanagement.auth.application.dto.request;

import com.loanmanagement.auth.domain.model.RoleType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CreateUserRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldPassValidation_whenAllFieldsAreValid() {
        CreateUserRequest request = CreateUserRequest.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .fullName("Test User")
                .phoneNumber("9999999999")
                .roles(Set.of(RoleType.ADMIN))
                .build();

        Set<ConstraintViolation<CreateUserRequest>> violations =
                validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void shouldFailValidation_whenUsernameIsBlank() {
        CreateUserRequest request = CreateUserRequest.builder()
                .username("")
                .email("test@example.com")
                .password("password123")
                .fullName("Test User")
                .phoneNumber("9999999999")
                .roles(Set.of(RoleType.ADMIN))
                .build();

        Set<ConstraintViolation<CreateUserRequest>> violations =
                validator.validate(request);

        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("username"));
    }

    @Test
    void shouldFailValidation_whenEmailIsInvalid() {
        CreateUserRequest request = CreateUserRequest.builder()
                .username("testuser")
                .email("invalid-email")
                .password("password123")
                .fullName("Test User")
                .phoneNumber("9999999999")
                .roles(Set.of(RoleType.ADMIN))
                .build();

        Set<ConstraintViolation<CreateUserRequest>> violations =
                validator.validate(request);

        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("email"));
    }

    @Test
    void shouldFailValidation_whenPasswordTooShort() {
        CreateUserRequest request = CreateUserRequest.builder()
                .username("testuser")
                .email("test@example.com")
                .password("123")
                .fullName("Test User")
                .phoneNumber("9999999999")
                .roles(Set.of(RoleType.ADMIN))
                .build();

        Set<ConstraintViolation<CreateUserRequest>> violations =
                validator.validate(request);

        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("password"));
    }

    @Test
    void shouldFailValidation_whenRolesEmpty() {
        CreateUserRequest request = CreateUserRequest.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .fullName("Test User")
                .phoneNumber("9999999999")
                .roles(Set.of())
                .build();

        Set<ConstraintViolation<CreateUserRequest>> violations =
                validator.validate(request);

        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("roles"));
    }

    @Test
    void shouldFailValidation_whenPhoneNumberBlank() {
        CreateUserRequest request = CreateUserRequest.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .fullName("Test User")
                .phoneNumber("")
                .roles(Set.of(RoleType.ADMIN))
                .build();

        Set<ConstraintViolation<CreateUserRequest>> violations =
                validator.validate(request);

        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("phoneNumber"));
    }
}
