package com.loanmanagement.loanapproval.infrastructure.client;

import com.loanmanagement.common.dto.UserDetailsDTO;
import com.loanmanagement.common.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Client for communicating with Auth Service (User Service)
 */
@Component
public class UserServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceClient.class);

    private final RestTemplate restTemplate;
    private final String authServiceUrl;

    public UserServiceClient(
            RestTemplate restTemplate,
            @Value("${services.auth-service.url}") String authServiceUrl) {
        this.restTemplate = restTemplate;
        this.authServiceUrl = authServiceUrl;
    }

    /**
     * Get user details by ID
     */
    public UserDetailsDTO getUserById(Long userId) {
        logger.info("Fetching user details for user ID: {}", userId);

        String url = authServiceUrl + "/api/internal/users/" + userId;

        try {
            ResponseEntity<UserDetailsDTO> response = restTemplate.getForEntity(url, UserDetailsDTO.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                logger.info("Successfully fetched user details for user ID: {}", userId);
                return response.getBody();
            }

            throw new ResourceNotFoundException("User not found with ID: " + userId);
        } catch (Exception e) {
            logger.error("Error fetching user details for user ID: {}", userId, e);
            throw new ResourceNotFoundException("User not found with ID: " + userId);
        }
    }

    /**
     * Verify user has required role
     */
    public boolean userHasRole(Long userId, String roleName) {
        logger.info("Verifying if user {} has role {}", userId, roleName);

        UserDetailsDTO user = getUserById(userId);

        if (user.getRoles() == null) {
            return false;
        }

        boolean hasRole = user.getRoles().stream()
                .anyMatch(role -> role.equalsIgnoreCase(roleName));

        logger.info("User {} has role {}: {}", userId, roleName, hasRole);
        return hasRole;
    }

    /**
     * Verify user has any of the required roles
     */
    public boolean userHasAnyRole(Long userId, List<String> roleNames) {
        logger.info("Verifying if user {} has any of roles {}", userId, roleNames);

        UserDetailsDTO user = getUserById(userId);

        if (user.getRoles() == null) {
            return false;
        }

        boolean hasAnyRole = user.getRoles().stream()
                .anyMatch(role -> roleNames.stream().anyMatch(role::equalsIgnoreCase));

        logger.info("User {} has any of roles {}: {}", userId, roleNames, hasAnyRole);
        return hasAnyRole;
    }
}
