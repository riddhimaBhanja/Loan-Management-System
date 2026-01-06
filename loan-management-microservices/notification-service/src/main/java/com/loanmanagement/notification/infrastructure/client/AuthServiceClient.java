package com.loanmanagement.notification.infrastructure.client;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Client for communicating with Auth Service
 */
@Component
@Slf4j
public class AuthServiceClient {

    private final RestTemplate restTemplate;
    private final String authServiceUrl;

    public AuthServiceClient(
            RestTemplate restTemplate,
            @Value("${services.auth-service.url}") String authServiceUrl) {
        this.restTemplate = restTemplate;
        this.authServiceUrl = authServiceUrl;
    }

    /**
     * Get user details by ID
     */
    public UserDetailsDto getUserById(Long userId) {
        log.info("Fetching user details for user ID: {}", userId);

        String url = authServiceUrl + "/api/internal/users/" + userId;

        try {
            ResponseEntity<UserDetailsDto> response = restTemplate.getForEntity(url, UserDetailsDto.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.info("Successfully fetched user details for user ID: {}", userId);
                return response.getBody();
            }

            log.warn("User not found with ID: {}", userId);
            return null;
        } catch (Exception e) {
            log.error("Failed to fetch user details for user ID: {}. Error: {}", userId, e.getMessage());
            return null;
        }
    }

    /**
     * DTO for User Details (matches UserDetailsDTO from auth-service)
     */
    @Data
    public static class UserDetailsDto {
        private Long userId;
        private String username;
        private String email;
        private String firstName;
        private String lastName;
        private String phoneNumber;
    }
}
