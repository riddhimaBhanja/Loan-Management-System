package com.loanmanagement.loanapproval.infrastructure.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    private static final String jwtSecret =
            "this-is-a-very-secure-secret-key-for-jwt-testing-purpose-only";

    private SecretKey key;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "jwtSecret", jwtSecret);
        jwtUtil.init();

        key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    private String generateValidToken() {
        return Jwts.builder()
                .subject("testuser")
                .claim("userId", 1L)
                .claim("roles", "ADMIN,USER")
                .expiration(new Date(System.currentTimeMillis() + 60_000))
                .signWith(key)
                .compact();
    }

    private String generateExpiredToken() {
        return Jwts.builder()
                .subject("expireduser")
                .claim("userId", 2L)
                .claim("roles", "USER")
                .expiration(new Date(System.currentTimeMillis() - 60_000))
                .signWith(key)
                .compact();
    }

    @Test
    void getUsernameFromToken_success() {
        String token = generateValidToken();

        String username = jwtUtil.getUsernameFromToken(token);

        assertEquals("testuser", username);
    }

    @Test
    void getUserIdFromToken_success() {
        String token = generateValidToken();

        Long userId = jwtUtil.getUserIdFromToken(token);

        assertEquals(1L, userId);
    }

    @Test
    void getRolesFromToken_success() {
        String token = generateValidToken();

        String roles = jwtUtil.getRolesFromToken(token);

        assertEquals("ADMIN,USER", roles);
    }

    @Test
    void validateToken_returnsTrue_forValidToken() {
        String token = generateValidToken();

        boolean result = jwtUtil.validateToken(token);

        assertTrue(result);
    }

    @Test
    void validateToken_returnsFalse_forExpiredToken() {
        String token = generateExpiredToken();

        boolean result = jwtUtil.validateToken(token);

        assertFalse(result);
    }

    @Test
    void getExpirationFromToken_success() {
        String token = generateValidToken();

        Date expiration = jwtUtil.getExpirationFromToken(token);

        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }

    @Test
    void isTokenExpired_returnsFalse_forValidToken() {
        String token = generateValidToken();

        boolean expired = jwtUtil.isTokenExpired(token);

        assertFalse(expired);
    }

    @Test
    void isTokenExpired_returnsTrue_forExpiredToken() {
        String token = generateExpiredToken();

        boolean expired = jwtUtil.isTokenExpired(token);

        assertTrue(expired);
    }

    @Test
    void invalidToken_returnsSafeDefaults() {
        String invalidToken = "invalid.jwt.token";

        assertNull(jwtUtil.getUsernameFromToken(invalidToken));
        assertNull(jwtUtil.getUserIdFromToken(invalidToken));
        assertEquals("", jwtUtil.getRolesFromToken(invalidToken));
        assertFalse(jwtUtil.validateToken(invalidToken));
        assertNull(jwtUtil.getExpirationFromToken(invalidToken));
        assertTrue(jwtUtil.isTokenExpired(invalidToken));
    }
}
