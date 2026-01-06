package com.loanmanagement.loanapp.infrastructure.client;

import com.loanmanagement.common.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Client for calling Auth Service (User Management)
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UserServiceClient {

    private final RestTemplate restTemplate;

    @Value("${services.auth-service.url}")
    private String authServiceUrl;

    /**
     * Get user details by ID
     */
    public UserResponse getUserById(Long userId) {
        try {
            String url = authServiceUrl + "/api/internal/users/" + userId;
            log.debug("Calling auth-service to get user: {}", url);
            return restTemplate.getForObject(url, UserResponse.class);
        } catch (Exception e) {
            log.error("Error calling auth-service for user {}: {}", userId, e.getMessage());
            return null;
        }
    }

    /**
     * Validate if user exists
     */
    public boolean userExists(Long userId) {
        try {
            UserResponse user = getUserById(userId);
            return user != null;
        } catch (Exception e) {
            log.error("Error validating user existence for ID {}: {}", userId, e.getMessage());
            return false;
        }
    }
}
