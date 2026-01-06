package com.loanmanagement.auth.shared.constants;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

class ApiConstantsTest {

    @Test
    void shouldHavePrivateConstructor() throws Exception {
        Constructor<ApiConstants> constructor =
                ApiConstants.class.getDeclaredConstructor();

        assertTrue(Modifier.isPrivate(constructor.getModifiers()));

        constructor.setAccessible(true);
        constructor.newInstance(); // should not throw
    }

    @Test
    void shouldMatchApiBasePaths() {
        assertEquals("/api", ApiConstants.API_BASE_PATH);
        assertEquals("/api/auth", ApiConstants.AUTH_BASE_PATH);
        assertEquals("/api/users", ApiConstants.USER_BASE_PATH);
        assertEquals("/api/internal/users", ApiConstants.INTERNAL_USER_BASE_PATH);
    }

    @Test
    void shouldMatchPaginationDefaults() {
        assertEquals("0", ApiConstants.DEFAULT_PAGE_NUMBER);
        assertEquals("10", ApiConstants.DEFAULT_PAGE_SIZE);
        assertEquals("createdAt", ApiConstants.DEFAULT_SORT_BY);
        assertEquals("desc", ApiConstants.DEFAULT_SORT_DIRECTION);
    }

    @Test
    void shouldMatchDateFormats() {
        assertEquals("yyyy-MM-dd", ApiConstants.DATE_FORMAT);
        assertEquals("yyyy-MM-dd HH:mm:ss", ApiConstants.DATE_TIME_FORMAT);
    }
}
