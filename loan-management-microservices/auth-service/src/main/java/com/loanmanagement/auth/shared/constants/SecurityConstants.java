package com.loanmanagement.auth.shared.constants;

/**
 * Security-related constants
 */
public final class SecurityConstants {

    private SecurityConstants() {
        // Private constructor to prevent instantiation
    }

    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String TOKEN_TYPE = "JWT";

    // Token expiration times (in milliseconds)
    public static final long ACCESS_TOKEN_VALIDITY = 24 * 60 * 60 * 1000;  // 24 hours
    public static final long REFRESH_TOKEN_VALIDITY = 7 * 24 * 60 * 60 * 1000;  // 7 days

    // Authorities/Roles
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_LOAN_OFFICER = "ROLE_LOAN_OFFICER";
    public static final String ROLE_CUSTOMER = "ROLE_CUSTOMER";

    // Public endpoints
    public static final String[] PUBLIC_URLS = {
            "/api/auth/**",
            "/api/internal/**",  // Allow internal APIs for inter-service communication
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/webjars/**",
            "/api-docs/**"
    };
}
