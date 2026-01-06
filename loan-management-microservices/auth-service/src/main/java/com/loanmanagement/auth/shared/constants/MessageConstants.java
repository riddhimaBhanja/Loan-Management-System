package com.loanmanagement.auth.shared.constants;

/**
 * Message constants for API responses
 */
public final class MessageConstants {

    private MessageConstants() {
        // Private constructor to prevent instantiation
    }

    // Authentication Messages
    public static final String LOGIN_SUCCESS = "Login successful";
    public static final String LOGOUT_SUCCESS = "Logout successful";
    public static final String REGISTRATION_SUCCESS = "User registered successfully";
    public static final String TOKEN_REFRESHED = "Token refreshed successfully";
    public static final String INVALID_CREDENTIALS = "Invalid username or password";
    public static final String USER_ALREADY_EXISTS = "User already exists with this username or email";

    // User Management Messages
    public static final String USER_CREATED = "User created successfully";
    public static final String USER_UPDATED = "User updated successfully";
    public static final String USER_DELETED = "User deactivated successfully";
    public static final String USER_NOT_FOUND = "User not found";

    // Validation Messages
    public static final String VALIDATION_FAILED = "Validation failed";
    public static final String REQUIRED_FIELD_MISSING = "Required field is missing";

    // Authorization Messages
    public static final String ACCESS_DENIED = "You don't have permission to perform this action";
    public static final String UNAUTHORIZED = "Unauthorized access";

    // General Messages
    public static final String OPERATION_SUCCESS = "Operation completed successfully";
    public static final String OPERATION_FAILED = "Operation failed";
    public static final String INTERNAL_SERVER_ERROR = "An internal server error occurred";
}
