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
import com.loanmanagement.auth.infrastructure.client.NotificationServiceClient;
import com.loanmanagement.auth.infrastructure.security.jwt.JwtTokenProvider;
import com.loanmanagement.common.exception.BusinessException;
import com.loanmanagement.common.exception.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of AuthService
 */
@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private NotificationServiceClient notificationServiceClient;

    @Override
    public AuthResponse register(RegisterRequest request) {
        logger.info("Registering new user: {}", request.getUsername());

        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("User already exists");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already exists");
        }

        // Map request to entity
        User user = userMapper.toEntity(request);

        // Hash password
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        // Assign CUSTOMER role by default
        UserRole customerRole = UserRole.builder()
                .role(RoleType.CUSTOMER)
                .build();
        user.addRole(customerRole);

        // Save user
        User savedUser = userRepository.save(user);

        logger.info("User registered successfully: {}", savedUser.getUsername());

        // Send account created notification (async, non-blocking)
        try {
            String fullName = savedUser.getFullName() != null ? savedUser.getFullName() : savedUser.getUsername();
            notificationServiceClient.sendAccountCreatedNotification(
                    savedUser.getEmail(),
                    fullName,
                    savedUser.getUsername()
            );
        } catch (Exception e) {
            logger.error("Failed to send account created notification for user: {}", savedUser.getUsername(), e);
            // Continue with registration flow even if notification fails
        }

        // Auto-login after registration
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate tokens
        String accessToken = jwtTokenProvider.generateToken(authentication, savedUser.getId());
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);

        UserResponse userResponse = userMapper.toResponse(savedUser);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(86400000L) // 24 hours
                .user(userResponse)
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        logger.info("User login attempt: {}", request.getUsername());

        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Get user details
        User user = userRepository.findByUsernameWithRoles(request.getUsername())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        // Generate tokens
        String accessToken = jwtTokenProvider.generateToken(authentication, user.getId());
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);

        UserResponse userResponse = userMapper.toResponse(user);

        logger.info("User logged in successfully: {}", request.getUsername());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(86400000L) // 24 hours
                .user(userResponse)
                .build();
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        logger.info("Refreshing access token");

        String refreshToken = request.getRefreshToken();

        // Validate refresh token
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new UnauthorizedException("Invalid or expired refresh token");
        }

        // Get username from token
        String username = jwtTokenProvider.getUsernameFromToken(refreshToken);

        // Get user details
        User user = userRepository.findByUsernameWithRoles(username)
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        // Extract roles
        String roles = user.getRoles().stream()
                .map(role -> role.getRole().getRoleName())
                .reduce((a, b) -> a + "," + b)
                .orElse("");

        // Generate new access token with userId and roles
        String newAccessToken = jwtTokenProvider.generateTokenWithClaims(username, user.getId(), roles, 86400000L);

        UserResponse userResponse = userMapper.toResponse(user);

        logger.info("Access token refreshed for user: {}", username);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(86400000L)
                .user(userResponse)
                .build();
    }

    @Override
    public void logout(String token) {
        // In a stateless JWT system, logout is handled on the client side
        // by removing the token. For additional security, you could:
        // 1. Maintain a blacklist of invalidated tokens (Redis)
        // 2. Use short-lived tokens and refresh tokens
        logger.info("User logout");
        SecurityContextHolder.clearContext();
    }
}
