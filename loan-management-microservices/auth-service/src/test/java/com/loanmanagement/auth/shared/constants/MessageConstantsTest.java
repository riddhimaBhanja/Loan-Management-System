package com.loanmanagement.auth.shared.constants;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

class MessageConstantsTest {

    @Test
    void shouldHavePrivateConstructor() throws Exception {
        Constructor<MessageConstants> constructor =
                MessageConstants.class.getDeclaredConstructor();

        assertTrue(Modifier.isPrivate(constructor.getModifiers()));

        constructor.setAccessible(true);
        constructor.newInstance(); // should not throw
    }

    @Test
    void shouldMatchAuthenticationMessages() {
        assertEquals("Login successful", MessageConstants.LOGIN_SUCCESS);
        assertEquals("Logout successful", MessageConstants.LOGOUT_SUCCESS);
        assertEquals("User registered successfully", MessageConstants.REGISTRATION_SUCCESS);
        assertEquals("Token refreshed successfully", MessageConstants.TOKEN_REFRESHED);
        assertEquals("Invalid username or password", MessageConstants.INVALID_CREDENTIALS);
        assertEquals(
                "User already exists with this username or email",
                MessageConstants.USER_ALREADY_EXISTS
        );
    }

    @Test
    void shouldMatchUserManagementMessages() {
        assertEquals("User created successfully", MessageConstants.USER_CREATED);
        assertEquals("User updated successfully", MessageConstants.USER_UPDATED);
        assertEquals("User deactivated successfully", MessageConstants.USER_DELETED);
        assertEquals("User not found", MessageConstants.USER_NOT_FOUND);
    }

    @Test
    void shouldMatchValidationMessages() {
        assertEquals("Validation failed", MessageConstants.VALIDATION_FAILED);
        assertEquals("Required field is missing", MessageConstants.REQUIRED_FIELD_MISSING);
    }

    @Test
    void shouldMatchAuthorizationMessages() {
        assertEquals(
                "You don't have permission to perform this action",
                MessageConstants.ACCESS_DENIED
        );
        assertEquals("Unauthorized access", MessageConstants.UNAUTHORIZED);
    }

    @Test
    void shouldMatchGeneralMessages() {
        assertEquals("Operation completed successfully", MessageConstants.OPERATION_SUCCESS);
        assertEquals("Operation failed", MessageConstants.OPERATION_FAILED);
        assertEquals(
                "An internal server error occurred",
                MessageConstants.INTERNAL_SERVER_ERROR
        );
    }
}
