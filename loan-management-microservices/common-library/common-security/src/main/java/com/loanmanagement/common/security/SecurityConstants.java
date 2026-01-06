package com.loanmanagement.common.security;

/**
 * Security Constants shared across microservices
 */
public class SecurityConstants {

    // JWT Header
    public static final String HEADER_STRING = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";

    // Roles
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_LOAN_OFFICER = "ROLE_LOAN_OFFICER";
    public static final String ROLE_CUSTOMER = "ROLE_CUSTOMER";

    // Role names without prefix
    public static final String ADMIN = "ADMIN";
    public static final String LOAN_OFFICER = "LOAN_OFFICER";
    public static final String CUSTOMER = "CUSTOMER";

    private SecurityConstants() {
        // Private constructor to prevent instantiation
    }
}
