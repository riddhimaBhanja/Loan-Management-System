package com.loanmanagement.auth.application.controller;

import com.loanmanagement.auth.application.dto.request.LoginRequest;
import com.loanmanagement.auth.application.dto.request.RefreshTokenRequest;
import com.loanmanagement.auth.application.dto.request.RegisterRequest;
import com.loanmanagement.auth.application.dto.response.AuthResponse;
import com.loanmanagement.auth.application.dto.response.UserResponse;
import com.loanmanagement.auth.domain.model.RoleType;
import com.loanmanagement.auth.domain.service.AuthService;
import com.loanmanagement.auth.shared.constants.ApiConstants;
import com.loanmanagement.auth.shared.constants.MessageConstants;
import com.loanmanagement.common.exception.BusinessException;
import com.loanmanagement.common.exception.UnauthorizedException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller tests for AuthController
 * Uses @WebMvcTest for controller layer testing
 * Target Coverage: 95%+
 */
@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@DisplayName("AuthController Tests")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private RefreshTokenRequest refreshTokenRequest;
    private AuthResponse authResponse;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        // Setup register request
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setPassword("password123");
        registerRequest.setEmail("test@example.com");
        registerRequest.setFullName("Test User");
        registerRequest.setPhoneNumber("+1234567890");

        // Setup login request
        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        // Setup refresh token request
        refreshTokenRequest = new RefreshTokenRequest();
        refreshTokenRequest.setRefreshToken("valid-refresh-token");

        // Setup user response
        userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setUsername("testuser");
        userResponse.setEmail("test@example.com");
        userResponse.setFullName("Test User");
        userResponse.setRoles(Set.of(RoleType.CUSTOMER));

        // Setup auth response
        authResponse = new AuthResponse();
        authResponse.setAccessToken("access-token");
        authResponse.setRefreshToken("refresh-token");
        authResponse.setTokenType("Bearer");
        authResponse.setExpiresIn(86400000L);
        authResponse.setUser(userResponse);
    }

    // ===================== REGISTER TESTS =====================

    @Test
    @DisplayName("Should register new customer successfully")
    void register_ShouldReturnCreated_WhenValidRequest() throws Exception {
        // Given
        when(authService.register(any(RegisterRequest.class))).thenReturn(authResponse);

        // When / Then
        mockMvc.perform(post(ApiConstants.AUTH_BASE_PATH + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(MessageConstants.REGISTRATION_SUCCESS))
                .andExpect(jsonPath("$.data.accessToken").value("access-token"))
                .andExpect(jsonPath("$.data.refreshToken").value("refresh-token"))
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.data.user.username").value("testuser"));

        verify(authService).register(any(RegisterRequest.class));
    }

    @Test
    @DisplayName("Should return error when username already exists during registration")
    void register_ShouldReturnBadRequest_WhenUsernameExists() throws Exception {
        // Given
        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new BusinessException("User already exists"));

        // When / Then
        mockMvc.perform(post(ApiConstants.AUTH_BASE_PATH + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest());

        verify(authService).register(any(RegisterRequest.class));
    }

    @Test
    @DisplayName("Should return error when email already exists during registration")
    void register_ShouldReturnBadRequest_WhenEmailExists() throws Exception {
        // Given
        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new BusinessException("Email already exists"));

        // When / Then
        mockMvc.perform(post(ApiConstants.AUTH_BASE_PATH + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest());

        verify(authService).register(any(RegisterRequest.class));
    }

    @Test
    @DisplayName("Should return validation error when registration request is invalid")
    void register_ShouldReturnBadRequest_WhenRequestInvalid() throws Exception {
        // Given - Invalid request (empty username)
        registerRequest.setUsername("");

        // When / Then
        mockMvc.perform(post(ApiConstants.AUTH_BASE_PATH + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).register(any());
    }

    // ===================== LOGIN TESTS =====================

    @Test
    @DisplayName("Should login successfully with valid credentials")
    void login_ShouldReturnOk_WhenCredentialsValid() throws Exception {
        // Given
        when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);

        // When / Then
        mockMvc.perform(post(ApiConstants.AUTH_BASE_PATH + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(MessageConstants.LOGIN_SUCCESS))
                .andExpect(jsonPath("$.data.accessToken").value("access-token"))
                .andExpect(jsonPath("$.data.refreshToken").value("refresh-token"))
                .andExpect(jsonPath("$.data.user.username").value("testuser"));

        verify(authService).login(any(LoginRequest.class));
    }

    @Test
    @DisplayName("Should return error when login credentials are invalid")
    void login_ShouldReturnUnauthorized_WhenCredentialsInvalid() throws Exception {
        // Given
        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new UnauthorizedException("Invalid credentials"));

        // When / Then
        mockMvc.perform(post(ApiConstants.AUTH_BASE_PATH + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());

        verify(authService).login(any(LoginRequest.class));
    }

    @Test
    @DisplayName("Should return validation error when login request is invalid")
    void login_ShouldReturnBadRequest_WhenRequestInvalid() throws Exception {
        // Given - Invalid request (empty username)
        loginRequest.setUsername("");

        // When / Then
        mockMvc.perform(post(ApiConstants.AUTH_BASE_PATH + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).login(any());
    }

    @Test
    @DisplayName("Should return validation error when password is empty")
    void login_ShouldReturnBadRequest_WhenPasswordEmpty() throws Exception {
        // Given
        loginRequest.setPassword("");

        // When / Then
        mockMvc.perform(post(ApiConstants.AUTH_BASE_PATH + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).login(any());
    }

    // ===================== REFRESH TOKEN TESTS =====================

    @Test
    @DisplayName("Should refresh token successfully with valid refresh token")
    void refreshToken_ShouldReturnOk_WhenTokenValid() throws Exception {
        // Given
        when(authService.refreshToken(any(RefreshTokenRequest.class))).thenReturn(authResponse);

        // When / Then
        mockMvc.perform(post(ApiConstants.AUTH_BASE_PATH + "/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshTokenRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(MessageConstants.TOKEN_REFRESHED))
                .andExpect(jsonPath("$.data.accessToken").value("access-token"))
                .andExpect(jsonPath("$.data.refreshToken").value("refresh-token"));

        verify(authService).refreshToken(any(RefreshTokenRequest.class));
    }

    @Test
    @DisplayName("Should return error when refresh token is invalid")
    void refreshToken_ShouldReturnUnauthorized_WhenTokenInvalid() throws Exception {
        // Given
        when(authService.refreshToken(any(RefreshTokenRequest.class)))
                .thenThrow(new UnauthorizedException("Invalid or expired refresh token"));

        // When / Then
        mockMvc.perform(post(ApiConstants.AUTH_BASE_PATH + "/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshTokenRequest)))
                .andExpect(status().isUnauthorized());

        verify(authService).refreshToken(any(RefreshTokenRequest.class));
    }

    @Test
    @DisplayName("Should return error when refresh token is expired")
    void refreshToken_ShouldReturnUnauthorized_WhenTokenExpired() throws Exception {
        // Given
        when(authService.refreshToken(any(RefreshTokenRequest.class)))
                .thenThrow(new UnauthorizedException("Invalid or expired refresh token"));

        // When / Then
        mockMvc.perform(post(ApiConstants.AUTH_BASE_PATH + "/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshTokenRequest)))
                .andExpect(status().isUnauthorized());

        verify(authService).refreshToken(any(RefreshTokenRequest.class));
    }

    @Test
    @DisplayName("Should return validation error when refresh token is empty")
    void refreshToken_ShouldReturnBadRequest_WhenTokenEmpty() throws Exception {
        // Given
        refreshTokenRequest.setRefreshToken("");

        // When / Then
        mockMvc.perform(post(ApiConstants.AUTH_BASE_PATH + "/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshTokenRequest)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).refreshToken(any());
    }

    // ===================== LOGOUT TESTS =====================

    @Test
    @DisplayName("Should logout successfully with authorization header")
    void logout_ShouldReturnOk_WhenTokenProvided() throws Exception {
        // Given
        doNothing().when(authService).logout(anyString());

        // When / Then
        mockMvc.perform(post(ApiConstants.AUTH_BASE_PATH + "/logout")
                        .header("Authorization", "Bearer access-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(MessageConstants.LOGOUT_SUCCESS));

        verify(authService).logout(anyString());
    }

    @Test
    @DisplayName("Should logout successfully without authorization header")
    void logout_ShouldReturnOk_WhenNoTokenProvided() throws Exception {
        // When / Then
        mockMvc.perform(post(ApiConstants.AUTH_BASE_PATH + "/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(MessageConstants.LOGOUT_SUCCESS));

        verify(authService, never()).logout(anyString());
    }

    // ===================== CONTENT TYPE TESTS =====================

    @Test
    @DisplayName("Should reject request with wrong content type")
    void register_ShouldReturnUnsupportedMediaType_WhenWrongContentType() throws Exception {
        // When / Then
        mockMvc.perform(post(ApiConstants.AUTH_BASE_PATH + "/register")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isUnsupportedMediaType());

        verify(authService, never()).register(any());
    }

    // ===================== MALFORMED JSON TESTS =====================

    @Test
    @DisplayName("Should return bad request for malformed JSON")
    void register_ShouldReturnBadRequest_WhenJsonMalformed() throws Exception {
        // Given
        String malformedJson = "{username: 'test', email: 'invalid}"; // Invalid JSON

        // When / Then
        mockMvc.perform(post(ApiConstants.AUTH_BASE_PATH + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andExpect(status().isBadRequest());

        verify(authService, never()).register(any());
    }

    // ===================== ENDPOINT PATH TESTS =====================

    @Test
    @DisplayName("Should handle register endpoint with correct path")
    void register_ShouldUseCorrectPath() throws Exception {
        // Given
        when(authService.register(any(RegisterRequest.class))).thenReturn(authResponse);

        // When / Then
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        verify(authService).register(any(RegisterRequest.class));
    }

    @Test
    @DisplayName("Should handle login endpoint with correct path")
    void login_ShouldUseCorrectPath() throws Exception {
        // Given
        when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);

        // When / Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk());

        verify(authService).login(any(LoginRequest.class));
    }
}
