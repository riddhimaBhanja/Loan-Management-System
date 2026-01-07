package com.loanmanagement.auth.domain.service;

import com.loanmanagement.auth.application.dto.request.LoginRequest;
import com.loanmanagement.auth.application.dto.request.RefreshTokenRequest;
import com.loanmanagement.auth.application.dto.request.RegisterRequest;
import com.loanmanagement.auth.application.dto.response.AuthResponse;
import com.loanmanagement.auth.application.dto.response.UserResponse;
import com.loanmanagement.auth.application.mapper.UserMapper;
import com.loanmanagement.auth.domain.model.User;
import com.loanmanagement.auth.domain.model.UserRole;
import com.loanmanagement.auth.domain.model.RoleType;
import com.loanmanagement.auth.domain.repository.UserRepository;
import com.loanmanagement.auth.infrastructure.security.jwt.JwtTokenProvider;
import com.loanmanagement.common.exception.BusinessException;
import com.loanmanagement.common.exception.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuthServiceImpl
 * Target Coverage: 95%+
 * Follows monolithic test patterns with Mockito, JUnit 5, and AssertJ
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Tests")
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private RefreshTokenRequest refreshTokenRequest;
    private User testUser;
    private UserResponse userResponse;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        // Setup register request
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setFullName("Test User");
        registerRequest.setPhoneNumber("+1234567890");

        // Setup login request
        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        // Setup refresh token request
        refreshTokenRequest = new RefreshTokenRequest();
        refreshTokenRequest.setRefreshToken("valid-refresh-token");

        // Setup test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPasswordHash("$2a$10$encoded");
        testUser.setFullName("Test User");
        testUser.setPhoneNumber("+1234567890");
        testUser.setIsActive(true);
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());

        UserRole customerRole = new UserRole();
        customerRole.setId(1L);
        customerRole.setRole(RoleType.CUSTOMER);
        customerRole.setUser(testUser);

        Set<UserRole> roles = new HashSet<>();
        roles.add(customerRole);
        testUser.setRoles(roles);

        // Setup user response
        userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setUsername("testuser");
        userResponse.setEmail("test@example.com");

        // Setup authentication
        authentication = mock(Authentication.class);
    }

    // ===================== REGISTRATION TESTS =====================

    @Test
    @DisplayName("Should register new customer successfully")
    void register_ShouldCreateNewCustomer_WhenValidRequest() {
        // Given
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$encoded");
        when(userMapper.toEntity(any(RegisterRequest.class))).thenReturn(testUser);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtTokenProvider.generateToken(any(Authentication.class), anyLong())).thenReturn("access-token");
        when(jwtTokenProvider.generateRefreshToken(any(Authentication.class))).thenReturn("refresh-token");
        when(userMapper.toResponse(any(User.class))).thenReturn(userResponse);

        // When
        AuthResponse response = authService.register(registerRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
        assertThat(response.getTokenType()).isEqualTo("Bearer");
        assertThat(response.getExpiresIn()).isEqualTo(86400000L);
        assertThat(response.getUser()).isNotNull();

        verify(userRepository).existsByUsername("testuser");
        verify(userRepository).existsByEmail("test@example.com");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtTokenProvider).generateToken(any(Authentication.class), anyLong());
        verify(jwtTokenProvider).generateRefreshToken(any(Authentication.class));
        verify(userMapper).toResponse(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when username already exists")
    void register_ShouldThrowException_WhenUsernameExists() {
        // Given
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        // When / Then
        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("already exists");

        verify(userRepository).existsByUsername("testuser");
        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when email already exists")
    void register_ShouldThrowException_WhenEmailExists() {
        // Given
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        // When / Then
        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("already exists");

        verify(userRepository).existsByEmail("test@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should assign CUSTOMER role automatically on registration")
    void register_ShouldAssignCustomerRole_WhenRegistering() {
        // Given
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$encoded");
        when(userMapper.toEntity(any(RegisterRequest.class))).thenReturn(new User());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(1L); // Set ID to simulate saved entity
            // Verify CUSTOMER role was added
            assertThat(savedUser.getRoles()).isNotEmpty();
            assertThat(savedUser.getRoles().stream()
                    .anyMatch(role -> role.getRole() == RoleType.CUSTOMER)).isTrue();
            return savedUser;
        });
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtTokenProvider.generateToken(any(Authentication.class), any())).thenReturn("token");
        when(jwtTokenProvider.generateRefreshToken(any(Authentication.class))).thenReturn("refresh");
        when(userMapper.toResponse(any(User.class))).thenReturn(userResponse);

        // When
        authService.register(registerRequest);

        // Then
        verify(userRepository).save(any(User.class));
    }

    // ===================== LOGIN TESTS =====================

    @Test
    @DisplayName("Should login successfully with valid credentials")
    void login_ShouldReturnAuthResponse_WhenCredentialsValid() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userRepository.findByUsernameWithRoles("testuser")).thenReturn(Optional.of(testUser));
        when(jwtTokenProvider.generateToken(any(Authentication.class), anyLong())).thenReturn("access-token");
        when(jwtTokenProvider.generateRefreshToken(any(Authentication.class))).thenReturn("refresh-token");
        when(userMapper.toResponse(any(User.class))).thenReturn(userResponse);

        // When
        AuthResponse response = authService.login(loginRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
        assertThat(response.getTokenType()).isEqualTo("Bearer");
        assertThat(response.getExpiresIn()).isEqualTo(86400000L);
        assertThat(response.getUser()).isNotNull();

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByUsernameWithRoles("testuser");
        verify(jwtTokenProvider).generateToken(eq(authentication), anyLong());
        verify(jwtTokenProvider).generateRefreshToken(authentication);
        verify(userMapper).toResponse(testUser);
    }

    @Test
    @DisplayName("Should throw exception when user not found after authentication")
    void login_ShouldThrowException_WhenUserNotFound() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userRepository.findByUsernameWithRoles("testuser")).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Invalid credentials");

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByUsernameWithRoles("testuser");
        verify(jwtTokenProvider, never()).generateToken(any(), anyLong());
    }

    // ===================== REFRESH TOKEN TESTS =====================

    @Test
    @DisplayName("Should refresh token successfully with valid refresh token")
    void refreshToken_ShouldReturnNewTokens_WhenRefreshTokenValid() {
        // Given
        when(jwtTokenProvider.validateToken("valid-refresh-token")).thenReturn(true);
        when(jwtTokenProvider.getUsernameFromToken("valid-refresh-token")).thenReturn("testuser");
        when(userRepository.findByUsernameWithRoles("testuser")).thenReturn(Optional.of(testUser));
        when(jwtTokenProvider.generateTokenWithClaims(anyString(), anyLong(), anyString(), anyLong())).thenReturn("new-access-token");
        when(userMapper.toResponse(any(User.class))).thenReturn(userResponse);

        // When
        AuthResponse response = authService.refreshToken(refreshTokenRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("new-access-token");
        assertThat(response.getRefreshToken()).isEqualTo("valid-refresh-token");
        assertThat(response.getTokenType()).isEqualTo("Bearer");
        assertThat(response.getExpiresIn()).isEqualTo(86400000L);

        verify(jwtTokenProvider).validateToken("valid-refresh-token");
        verify(jwtTokenProvider).getUsernameFromToken("valid-refresh-token");
        verify(userRepository).findByUsernameWithRoles("testuser");
        verify(jwtTokenProvider).generateTokenWithClaims(anyString(), anyLong(), anyString(), anyLong());
    }

    @Test
    @DisplayName("Should throw exception when refresh token is invalid")
    void refreshToken_ShouldThrowException_WhenTokenInvalid() {
        // Given
        refreshTokenRequest.setRefreshToken("invalid-token");
        when(jwtTokenProvider.validateToken("invalid-token")).thenReturn(false);

        // When / Then
        assertThatThrownBy(() -> authService.refreshToken(refreshTokenRequest))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Invalid or expired refresh token");

        verify(jwtTokenProvider).validateToken("invalid-token");
        verify(jwtTokenProvider, never()).getUsernameFromToken(anyString());
        verify(userRepository, never()).findByUsernameWithRoles(anyString());
    }

    @Test
    @DisplayName("Should throw exception when refresh token is expired")
    void refreshToken_ShouldThrowException_WhenTokenExpired() {
        // Given
        refreshTokenRequest.setRefreshToken("expired-token");
        when(jwtTokenProvider.validateToken("expired-token")).thenReturn(false);

        // When / Then
        assertThatThrownBy(() -> authService.refreshToken(refreshTokenRequest))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Invalid or expired");

        verify(jwtTokenProvider).validateToken("expired-token");
        verify(userRepository, never()).findByUsernameWithRoles(anyString());
    }

    @Test
    @DisplayName("Should throw exception when user not found during token refresh")
    void refreshToken_ShouldThrowException_WhenUserNotFound() {
        // Given
        when(jwtTokenProvider.validateToken("valid-refresh-token")).thenReturn(true);
        when(jwtTokenProvider.getUsernameFromToken("valid-refresh-token")).thenReturn("testuser");
        when(userRepository.findByUsernameWithRoles("testuser")).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> authService.refreshToken(refreshTokenRequest))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("User not found");

        verify(jwtTokenProvider).validateToken("valid-refresh-token");
        verify(jwtTokenProvider).getUsernameFromToken("valid-refresh-token");
        verify(userRepository).findByUsernameWithRoles("testuser");
    }

    // ===================== LOGOUT TESTS =====================

    @Test
    @DisplayName("Should logout successfully")
    void logout_ShouldClearSecurityContext_WhenCalled() {
        // Given
        String token = "Bearer valid-token";

        // When
        authService.logout(token);

        // Then - verify no exceptions thrown
        // Note: logout is stateless in JWT, just clears context
        verifyNoInteractions(userRepository);
        verifyNoInteractions(jwtTokenProvider);
    }

    @Test
    @DisplayName("Should handle logout with null token")
    void logout_ShouldHandleNullToken_WhenTokenIsNull() {
        // When
        authService.logout(null);

        // Then - verify no exceptions thrown
        verifyNoInteractions(userRepository);
        verifyNoInteractions(jwtTokenProvider);
    }
}
