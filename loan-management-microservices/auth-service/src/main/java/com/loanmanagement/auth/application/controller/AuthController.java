package com.loanmanagement.auth.application.controller;

import com.loanmanagement.auth.application.dto.request.LoginRequest;

import com.loanmanagement.auth.application.dto.request.RefreshTokenRequest;
import com.loanmanagement.auth.application.dto.request.RegisterRequest;
import com.loanmanagement.auth.application.dto.response.ApiResponse;
import com.loanmanagement.auth.application.dto.response.AuthResponse;
import com.loanmanagement.auth.domain.service.AuthService;
import com.loanmanagement.auth.shared.constants.ApiConstants;
import com.loanmanagement.auth.shared.constants.MessageConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for authentication endpoints
 */
@RestController
@RequestMapping(ApiConstants.AUTH_BASE_PATH)
@Tag(name = "Authentication", description = "Authentication and registration APIs")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register new customer", description = "Register a new customer account")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        logger.info("POST /api/auth/register - Username: {}", request.getUsername());

        AuthResponse response = authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(MessageConstants.REGISTRATION_SUCCESS, response));
    }

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user and receive JWT tokens")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        logger.info("POST /api/auth/login - Username: {}", request.getUsername());

        AuthResponse response = authService.login(request);

        return ResponseEntity.ok(ApiResponse.success(MessageConstants.LOGIN_SUCCESS, response));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", description = "Get new access token using refresh token")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        logger.info("POST /api/auth/refresh");

        AuthResponse response = authService.refreshToken(request);

        return ResponseEntity.ok(ApiResponse.success(MessageConstants.TOKEN_REFRESHED, response));
    }

    @PostMapping("/logout")
    @Operation(summary = "User logout", description = "Logout current user")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestHeader(value = "Authorization", required = false) String token) {
        logger.info("POST /api/auth/logout");

        if (token != null) {
            authService.logout(token);
        }

        return ResponseEntity.ok(ApiResponse.success(MessageConstants.LOGOUT_SUCCESS));
    }
}
