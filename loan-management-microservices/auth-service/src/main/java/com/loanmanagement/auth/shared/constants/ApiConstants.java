package com.loanmanagement.auth.shared.constants;

/**
 * API-related constants
 */
public final class ApiConstants {

    private ApiConstants() {
        // Private constructor to prevent instantiation
    }

    // API Base Paths
    public static final String API_BASE_PATH = "/api";
    public static final String AUTH_BASE_PATH = API_BASE_PATH + "/auth";
    public static final String USER_BASE_PATH = API_BASE_PATH + "/users";
    public static final String INTERNAL_USER_BASE_PATH = API_BASE_PATH + "/internal/users";

    // Pagination Defaults
    public static final String DEFAULT_PAGE_NUMBER = "0";
    public static final String DEFAULT_PAGE_SIZE = "10";
    public static final String DEFAULT_SORT_BY = "createdAt";
    public static final String DEFAULT_SORT_DIRECTION = "desc";

    // Date Format
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
}
