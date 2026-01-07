package com.loanmanagement.auth.application.dto.response;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthResponseTest {

    @Test
    void shouldCreateAuthResponseWithAllFields() {
        UserResponse user = UserResponse.builder()
                .id(1L)
                .username("john_doe")
                .email("john@example.com")
                .build();

        AuthResponse response = AuthResponse.builder()
                .accessToken("access-token")
                .refreshToken("refresh-token")
                .expiresIn(3600L)
                .user(user)
                .build();

        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals(3600L, response.getExpiresIn());
        assertEquals(user, response.getUser());
    }

    @Test
    void shouldUseDefaultTokenTypeWhenNotProvided() {
        AuthResponse response = AuthResponse.builder()
                .accessToken("access-token")
                .build();

        assertEquals("Bearer", response.getTokenType());
    }

    @Test
    void shouldAllowCustomTokenType() {
        AuthResponse response = AuthResponse.builder()
                .tokenType("JWT")
                .build();

        assertEquals("JWT", response.getTokenType());
    }

    @Test
    void shouldSupportNoArgsConstructor() {
        AuthResponse response = new AuthResponse();

        assertNull(response.getAccessToken());
        assertNull(response.getRefreshToken());
        assertEquals("Bearer", response.getTokenType());
        assertNull(response.getExpiresIn());
        assertNull(response.getUser());
    }

    @Test
    void shouldSupportAllArgsConstructor() {
        UserResponse user = new UserResponse();

        AuthResponse response = new AuthResponse(
                "access",
                "refresh",
                "Bearer",
                1800L,
                user
        );

        assertEquals("access", response.getAccessToken());
        assertEquals("refresh", response.getRefreshToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals(1800L, response.getExpiresIn());
        assertEquals(user, response.getUser());
    }
    @Test
    void equalsAndHashCode_shouldWorkCorrectly() {
        UserResponse user = UserResponse.builder()
                .id(1L)
                .username("john")
                .email("john@test.com")
                .build();

        AuthResponse r1 = AuthResponse.builder()
                .accessToken("access")
                .refreshToken("refresh")
                .tokenType("Bearer")
                .expiresIn(3600L)
                .user(user)
                .build();

        AuthResponse r2 = AuthResponse.builder()
                .accessToken("access")
                .refreshToken("refresh")
                .tokenType("Bearer")
                .expiresIn(3600L)
                .user(user)
                .build();

        AuthResponse r3 = AuthResponse.builder()
                .accessToken("different")
                .build();

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
        assertNotEquals(r1, r3);
    }

    @Test
    void equals_shouldReturnFalseForNullAndDifferentType() {
        AuthResponse response = AuthResponse.builder()
                .accessToken("access")
                .build();

        assertNotEquals(response, null);
        assertNotEquals(response, "not-auth-response");
    }

    @Test
    void toString_shouldContainClassNameAndFields() {
        AuthResponse response = AuthResponse.builder()
                .accessToken("access")
                .refreshToken("refresh")
                .build();

        String value = response.toString();

        assertNotNull(value);
        assertTrue(value.contains("AuthResponse"));
        assertTrue(value.contains("access"));
    }

}
