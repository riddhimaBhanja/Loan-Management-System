package com.loanmanagement.auth.infrastructure.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.Date;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for JwtTokenProvider
 * Target Coverage: 95%+
 * Follows monolithic test patterns with Mockito, JUnit 5, and AssertJ
 */
@DisplayName("JwtTokenProvider Tests")
class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    private Authentication authentication;
    private UserDetails userDetails;

    private static final String TEST_SECRET = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970"; // 64+ chars
    private static final Long TEST_EXPIRATION = 3600000L; // 1 hour
    private static final Long TEST_REFRESH_EXPIRATION = 86400000L; // 24 hours

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();

        // Set test properties using reflection
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", TEST_SECRET);
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpiration", TEST_EXPIRATION);
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtRefreshExpiration", TEST_REFRESH_EXPIRATION);

        // Initialize the JWT key
        jwtTokenProvider.init();

        // Setup mock authentication and user details
        userDetails = User.builder()
                .username("testuser")
                .password("password")
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_CUSTOMER")))
                .build();

        authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
    }

    // ===================== TOKEN GENERATION TESTS =====================

    @Test
    @DisplayName("Should generate access token from authentication")
    void generateToken_ShouldReturnValidToken_WhenAuthenticationProvided() {
        // When
        String token = jwtTokenProvider.generateToken(authentication, 1L);

        // Then
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        assertThat(token.split("\\.")).hasSize(3); // JWT has 3 parts
    }

    @Test
    @DisplayName("Should generate token from username and expiration")
    void generateTokenFromUsername_ShouldReturnValidToken_WhenUsernameProvided() {
        // Given
        String username = "testuser";
        Long expiration = 3600000L;

        // When
        String token = jwtTokenProvider.generateTokenFromUsername(username, expiration);

        // Then
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        assertThat(token.split("\\.")).hasSize(3);
    }

    @Test
    @DisplayName("Should generate refresh token from authentication")
    void generateRefreshToken_ShouldReturnValidToken_WhenAuthenticationProvided() {
        // When
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);

        // Then
        assertThat(refreshToken).isNotNull();
        assertThat(refreshToken).isNotEmpty();
        assertThat(refreshToken.split("\\.")).hasSize(3);
    }

    @Test
    @DisplayName("Should generate different tokens for access and refresh")
    void generateTokens_ShouldReturnDifferentTokens_ForAccessAndRefresh() {
        // When
        String accessToken = jwtTokenProvider.generateToken(authentication, 1L);
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);

        // Then
        assertThat(accessToken).isNotEqualTo(refreshToken);
    }

    // ===================== TOKEN VALIDATION TESTS =====================

    @Test
    @DisplayName("Should validate valid token successfully")
    void validateToken_ShouldReturnTrue_WhenTokenIsValid() {
        // Given
        String token = jwtTokenProvider.generateToken(authentication, 1L);

        // When
        boolean isValid = jwtTokenProvider.validateToken(token);

        // Then
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Should reject malformed token")
    void validateToken_ShouldReturnFalse_WhenTokenIsMalformed() {
        // Given
        String malformedToken = "invalid.token.here";

        // When
        boolean isValid = jwtTokenProvider.validateToken(malformedToken);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Should reject token with invalid signature")
    void validateToken_ShouldReturnFalse_WhenSignatureIsInvalid() {
        // Given
        String token = jwtTokenProvider.generateToken(authentication, 1L);
        String tamperedToken = token.substring(0, token.length() - 10) + "tamperedXX";

        // When
        boolean isValid = jwtTokenProvider.validateToken(tamperedToken);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Should reject empty token")
    void validateToken_ShouldReturnFalse_WhenTokenIsEmpty() {
        // Given
        String emptyToken = "";

        // When
        boolean isValid = jwtTokenProvider.validateToken(emptyToken);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Should reject null token")
    void validateToken_ShouldReturnFalse_WhenTokenIsNull() {
        // When
        boolean isValid = jwtTokenProvider.validateToken(null);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Should reject expired token")
    void validateToken_ShouldReturnFalse_WhenTokenIsExpired() throws InterruptedException {
        // Given - Create token with very short expiration
        String shortLivedToken = jwtTokenProvider.generateTokenFromUsername("testuser", 1L); // 1ms

        // Wait for token to expire
        Thread.sleep(10);

        // When
        boolean isValid = jwtTokenProvider.validateToken(shortLivedToken);

        // Then
        assertThat(isValid).isFalse();
    }

    // ===================== EXTRACT USERNAME TESTS =====================

    @Test
    @DisplayName("Should extract username from valid token")
    void getUsernameFromToken_ShouldReturnUsername_WhenTokenIsValid() {
        // Given
        String token = jwtTokenProvider.generateToken(authentication, 1L);

        // When
        String username = jwtTokenProvider.getUsernameFromToken(token);

        // Then
        assertThat(username).isEqualTo("testuser");
    }

    @Test
    @DisplayName("Should extract correct username from token generated with username")
    void getUsernameFromToken_ShouldReturnCorrectUsername_ForTokenFromUsername() {
        // Given
        String expectedUsername = "customuser";
        String token = jwtTokenProvider.generateTokenFromUsername(expectedUsername, TEST_EXPIRATION);

        // When
        String username = jwtTokenProvider.getUsernameFromToken(token);

        // Then
        assertThat(username).isEqualTo(expectedUsername);
    }

    @Test
    @DisplayName("Should throw exception when extracting username from invalid token")
    void getUsernameFromToken_ShouldThrowException_WhenTokenIsInvalid() {
        // Given
        String invalidToken = "invalid.token.here";

        // When / Then
        assertThatThrownBy(() -> jwtTokenProvider.getUsernameFromToken(invalidToken))
                .isInstanceOf(Exception.class);
    }

    // ===================== TOKEN EXPIRATION TESTS =====================

    @Test
    @DisplayName("Should extract expiration date from token")
    void getExpirationFromToken_ShouldReturnDate_WhenTokenIsValid() {
        // Given
        String token = jwtTokenProvider.generateToken(authentication, 1L);

        // When
        Date expiration = jwtTokenProvider.getExpirationFromToken(token);

        // Then
        assertThat(expiration).isNotNull();
        assertThat(expiration).isAfter(new Date());
    }

    @Test
    @DisplayName("Should verify token is not expired for fresh token")
    void isTokenExpired_ShouldReturnFalse_WhenTokenIsFresh() {
        // Given
        String token = jwtTokenProvider.generateToken(authentication, 1L);

        // When
        boolean isExpired = jwtTokenProvider.isTokenExpired(token);

        // Then
        assertThat(isExpired).isFalse();
    }

    @Test
    @DisplayName("Should verify token is expired for old token")
    void isTokenExpired_ShouldReturnTrue_WhenTokenIsExpired() throws InterruptedException {
        // Given - Create token with very short expiration
        String shortLivedToken = jwtTokenProvider.generateTokenFromUsername("testuser", 1L); // 1ms

        // Wait for token to expire
        Thread.sleep(10);

        // When
        boolean isExpired = jwtTokenProvider.isTokenExpired(shortLivedToken);

        // Then
        assertThat(isExpired).isTrue();
    }

    @Test
    @DisplayName("Should return true for invalid token when checking expiration")
    void isTokenExpired_ShouldReturnTrue_WhenTokenIsInvalid() {
        // Given
        String invalidToken = "invalid.token.here";

        // When
        boolean isExpired = jwtTokenProvider.isTokenExpired(invalidToken);

        // Then
        assertThat(isExpired).isTrue();
    }

    @Test
    @DisplayName("Should throw exception when getting expiration from invalid token")
    void getExpirationFromToken_ShouldThrowException_WhenTokenIsInvalid() {
        // Given
        String invalidToken = "invalid.token.here";

        // When / Then
        assertThatThrownBy(() -> jwtTokenProvider.getExpirationFromToken(invalidToken))
                .isInstanceOf(Exception.class);
    }

    // ===================== GET EXPIRATION TIME =====================

    @Test
    @DisplayName("Should return configured expiration time")
    void getExpirationTime_ShouldReturnConfiguredValue() {
        // When
        Long expirationTime = jwtTokenProvider.getExpirationTime();

        // Then
        assertThat(expirationTime).isEqualTo(TEST_EXPIRATION);
    }

    // ===================== TOKEN CONSISTENCY TESTS =====================

    @Test
    @DisplayName("Should generate consistent tokens for same user")
    void generateToken_ShouldBeConsistent_ForSameUser() {
        // When
        String token1 = jwtTokenProvider.generateToken(authentication, 1L);
        String token2 = jwtTokenProvider.generateToken(authentication, 1L);

        // Extract usernames from both tokens
        String username1 = jwtTokenProvider.getUsernameFromToken(token1);
        String username2 = jwtTokenProvider.getUsernameFromToken(token2);

        // Then - usernames should be same (tokens will be different due to timestamp)
        assertThat(username1).isEqualTo(username2);
        assertThat(username1).isEqualTo("testuser");
    }

    @Test
    @DisplayName("Should validate token generated from username")
    void validateToken_ShouldReturnTrue_ForTokenGeneratedFromUsername() {
        // Given
        String token = jwtTokenProvider.generateTokenFromUsername("testuser", TEST_EXPIRATION);

        // When
        boolean isValid = jwtTokenProvider.validateToken(token);

        // Then
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Should extract same username that was used to generate token")
    void getUsernameFromToken_ShouldMatchOriginal_AfterGeneration() {
        // Given
        String originalUsername = "testuser123";
        String token = jwtTokenProvider.generateTokenFromUsername(originalUsername, TEST_EXPIRATION);

        // When
        String extractedUsername = jwtTokenProvider.getUsernameFromToken(token);

        // Then
        assertThat(extractedUsername).isEqualTo(originalUsername);
    }

    // ===================== REFRESH TOKEN TESTS =====================

    @Test
    @DisplayName("Should generate refresh token with longer expiration")
    void generateRefreshToken_ShouldHaveLongerExpiration_ThanAccessToken() {
        // Given
        String accessToken = jwtTokenProvider.generateToken(authentication, 1L);
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);

        // When
        Date accessExpiration = jwtTokenProvider.getExpirationFromToken(accessToken);
        Date refreshExpiration = jwtTokenProvider.getExpirationFromToken(refreshToken);

        // Then
        assertThat(refreshExpiration).isAfter(accessExpiration);
    }

    @Test
    @DisplayName("Should validate refresh token successfully")
    void validateToken_ShouldReturnTrue_ForRefreshToken() {
        // Given
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);

        // When
        boolean isValid = jwtTokenProvider.validateToken(refreshToken);

        // Then
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Should extract username from refresh token")
    void getUsernameFromToken_ShouldReturnUsername_ForRefreshToken() {
        // Given
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);

        // When
        String username = jwtTokenProvider.getUsernameFromToken(refreshToken);

        // Then
        assertThat(username).isEqualTo("testuser");
    }

    // ===================== EDGE CASES =====================

    @Test
    @DisplayName("Should handle token with special characters in username")
    void generateToken_ShouldHandleSpecialCharacters_InUsername() {
        // Given
        String specialUsername = "user.test+123@example.com";
        String token = jwtTokenProvider.generateTokenFromUsername(specialUsername, TEST_EXPIRATION);

        // When
        String extractedUsername = jwtTokenProvider.getUsernameFromToken(token);

        // Then
        assertThat(extractedUsername).isEqualTo(specialUsername);
        assertThat(jwtTokenProvider.validateToken(token)).isTrue();
    }

    @Test
    @DisplayName("Should handle very long username")
    void generateToken_ShouldHandleLongUsername() {
        // Given
        String longUsername = "a".repeat(100);
        String token = jwtTokenProvider.generateTokenFromUsername(longUsername, TEST_EXPIRATION);

        // When
        String extractedUsername = jwtTokenProvider.getUsernameFromToken(token);

        // Then
        assertThat(extractedUsername).isEqualTo(longUsername);
        assertThat(jwtTokenProvider.validateToken(token)).isTrue();
    }
}
