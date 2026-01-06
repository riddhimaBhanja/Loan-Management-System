package com.loanmanagement.auth.domain.service;

import com.loanmanagement.auth.application.dto.request.LoginRequest;
import com.loanmanagement.auth.application.dto.request.RefreshTokenRequest;
import com.loanmanagement.auth.application.dto.request.RegisterRequest;
import com.loanmanagement.auth.application.dto.response.AuthResponse;

/**
 * Service interface for authentication operations
 */
public interface AuthService {

    /**
     * Register a new customer
     */
    AuthResponse register(RegisterRequest request);

    /**
     * Login user and return JWT tokens
     */
    AuthResponse login(LoginRequest request);

    /**
     * Refresh access token using refresh token
     */
    AuthResponse refreshToken(RefreshTokenRequest request);

    /**
     * Logout user (invalidate tokens)
     */
    void logout(String token);
}
