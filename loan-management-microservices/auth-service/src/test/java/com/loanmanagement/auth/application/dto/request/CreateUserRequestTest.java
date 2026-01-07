package com.loanmanagement.auth.application.dto.request;

import com.loanmanagement.auth.domain.model.RoleType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CreateUserRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validCreateUserRequest_shouldHaveNoViolations() {
        CreateUserRequest request = CreateUserRequest.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .fullName("Test User")
                .phoneNumber("9876543210")
                .roles(Set.of(RoleType.ADMIN))
                .build();

        Set<ConstraintViolation<CreateUserRequest>> violations =
                validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void blankUsername_shouldFailValidation() {
        CreateUserRequest request = CreateUserRequest.builder()
                .username("")
                .email("test@example.com")
                .password("password123")
                .fullName("Test User")
                .phoneNumber("9876543210")
                .roles(Set.of(RoleType.ADMIN))
                .build();

        assertFalse(validator.validate(request).isEmpty());
    }

    @Test
    void invalidEmail_shouldFailValidation() {
        CreateUserRequest request = CreateUserRequest.builder()
                .username("testuser")
                .email("invalid-email")
                .password("password123")
                .fullName("Test User")
                .phoneNumber("9876543210")
                .roles(Set.of(RoleType.ADMIN))
                .build();

        assertFalse(validator.validate(request).isEmpty());
    }

    @Test
    void shortPassword_shouldFailValidation() {
        CreateUserRequest request = CreateUserRequest.builder()
                .username("testuser")
                .email("test@example.com")
                .password("123")
                .fullName("Test User")
                .phoneNumber("9876543210")
                .roles(Set.of(RoleType.ADMIN))
                .build();

        assertFalse(validator.validate(request).isEmpty());
    }

    @Test
    void emptyRoles_shouldFailValidation() {
        CreateUserRequest request = CreateUserRequest.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .fullName("Test User")
                .phoneNumber("9876543210")
                .roles(Collections.emptySet())
                .build();

        assertFalse(validator.validate(request).isEmpty());
    }
    @Test
    void equalsAndHashCode_shouldWorkCorrectly() {
        CreateUserRequest req1 = CreateUserRequest.builder()
                .username("user1")
                .email("user1@test.com")
                .password("password123")
                .fullName("User One")
                .phoneNumber("9876543210")
                .roles(Set.of(RoleType.ADMIN))
                .build();

        CreateUserRequest req2 = CreateUserRequest.builder()
                .username("user1")
                .email("user1@test.com")
                .password("password123")
                .fullName("User One")
                .phoneNumber("9876543210")
                .roles(Set.of(RoleType.ADMIN))
                .build();

        CreateUserRequest req3 = CreateUserRequest.builder()
                .username("user2")
                .email("user2@test.com")
                .password("password123")
                .fullName("User Two")
                .phoneNumber("9876543211")
                .roles(Set.of(RoleType.CUSTOMER))
                .build();

        assertEquals(req1, req2);
        assertEquals(req1.hashCode(), req2.hashCode());
        assertNotEquals(req1, req3);
    }

    @Test
    void equals_shouldReturnFalseForDifferentTypeAndNull() {
        CreateUserRequest request = CreateUserRequest.builder()
                .username("user")
                .email("user@test.com")
                .password("password123")
                .fullName("User")
                .phoneNumber("9876543210")
                .roles(Set.of(RoleType.ADMIN))
                .build();

        assertNotEquals(request, null);
        assertNotEquals(request, "some-string");
    }

    @Test
    void toString_shouldContainClassNameAndFields() {
        CreateUserRequest request = CreateUserRequest.builder()
                .username("user")
                .email("user@test.com")
                .password("password123")
                .fullName("User")
                .phoneNumber("9876543210")
                .roles(Set.of(RoleType.ADMIN))
                .build();

        String result = request.toString();

        assertNotNull(result);
        assertTrue(result.contains("CreateUserRequest"));
        assertTrue(result.contains("user"));
        assertTrue(result.contains("user@test.com"));
    }

    @Test
    void constructors_shouldCreateObjectsSuccessfully() {
        CreateUserRequest noArgs = new CreateUserRequest();
        assertNotNull(noArgs);

        CreateUserRequest allArgs = new CreateUserRequest(
                "user",
                "user@test.com",
                "password123",
                "User",
                "9876543210",
                Set.of(RoleType.ADMIN)
        );

        assertEquals("user", allArgs.getUsername());
        assertEquals("user@test.com", allArgs.getEmail());
    }

}
