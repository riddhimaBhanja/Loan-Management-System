package com.loanmanagement.auth.shared.constants;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

class SecurityConstantsTest {

    @Test
    void shouldHavePrivateConstructor() throws Exception {
        Constructor<SecurityConstants> constructor =
                SecurityConstants.class.getDeclaredConstructor();

        assertTrue(Modifier.isPrivate(constructor.getModifiers()));

        constructor.setAccessible(true);
        constructor.newInstance(); // should not throw
    }

    @Test
    void shouldMatchTokenConstants() {
        assertEquals("Bearer ", SecurityConstants.TOKEN_PREFIX);
        assertEquals("Authorization", SecurityConstants.HEADER_STRING);
        assertEquals("JWT", SecurityConstants.TOKEN_TYPE);
    }

    @Test
    void shouldMatchTokenValidityConstants() {
        assertEquals(24 * 60 * 60 * 1000, SecurityConstants.ACCESS_TOKEN_VALIDITY);
        assertEquals(7 * 24 * 60 * 60 * 1000, SecurityConstants.REFRESH_TOKEN_VALIDITY);
    }

    @Test
    void shouldMatchRoleConstants() {
        assertEquals("ROLE_ADMIN", SecurityConstants.ROLE_ADMIN);
        assertEquals("ROLE_LOAN_OFFICER", SecurityConstants.ROLE_LOAN_OFFICER);
        assertEquals("ROLE_CUSTOMER", SecurityConstants.ROLE_CUSTOMER);
    }

    @Test
    void shouldMatchPublicUrls() {
        assertNotNull(SecurityConstants.PUBLIC_URLS);
        assertTrue(SecurityConstants.PUBLIC_URLS.length > 0);

        String[] expectedUrls = {
                "/api/auth/**",
                "/api/internal/**",
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/swagger-resources/**",
                "/webjars/**",
                "/api-docs/**"
        };

        assertArrayEquals(expectedUrls, SecurityConstants.PUBLIC_URLS);
    }
}
