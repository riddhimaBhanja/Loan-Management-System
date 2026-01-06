package com.loanmanagement.auth.application.controller;

import com.loanmanagement.auth.domain.service.UserService;
import com.loanmanagement.auth.shared.constants.ApiConstants;
import com.loanmanagement.common.dto.UserDetailsDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Internal REST Controller for inter-service communication
 * These endpoints are meant to be called by other microservices only
 * and should bypass JWT authentication (configured in SecurityConfig)
 */
@RestController
@RequestMapping(ApiConstants.INTERNAL_USER_BASE_PATH)
@Tag(name = "Internal User API", description = "Internal APIs for inter-service communication")
public class InternalUserController {

    private static final Logger logger = LoggerFactory.getLogger(InternalUserController.class);

    @Autowired
    private UserService userService;

    /**
     * Get user details by ID
     * Called by other microservices to validate user existence and get user info
     */
    @GetMapping("/{userId}")
    @Operation(summary = "Get user by ID (Internal)", description = "Get user details by ID for inter-service communication")
    public UserDetailsDTO getUserById(@PathVariable Long userId) {
        logger.info("INTERNAL GET /api/internal/users/{}", userId);
        return userService.getUserDetailsById(userId);
    }

    /**
     * Get user ID by username
     * Called by other microservices to get user ID from username (e.g., from JWT token)
     */
    @GetMapping("/by-username")
    @Operation(summary = "Get user ID by username (Internal)", description = "Get user ID by username for inter-service communication")
    public Long getUserIdByUsername(@RequestParam String username) {
        logger.info("INTERNAL GET /api/internal/users/by-username?username={}", username);
        UserDetailsDTO userDetails = userService.getUserDetailsByUsername(username);
        return userDetails.getId();
    }

    /**
     * Get user name by ID
     * Called by other microservices to get user's full name for display purposes
     */
    @GetMapping("/{userId}/name")
    @Operation(summary = "Get user name by ID (Internal)", description = "Get user full name by ID for inter-service communication")
    public String getUserName(@PathVariable Long userId) {
        logger.info("INTERNAL GET /api/internal/users/{}/name", userId);
        UserDetailsDTO userDetails = userService.getUserDetailsById(userId);
        return userDetails.getFirstName() + " " + userDetails.getLastName();
    }

    /**
     * Check if user exists
     * Called by other microservices to validate user existence
     */
    @GetMapping("/{userId}/exists")
    @Operation(summary = "Check if user exists (Internal)", description = "Check if user exists by ID for inter-service communication")
    public boolean userExists(@PathVariable Long userId) {
        logger.info("INTERNAL GET /api/internal/users/{}/exists", userId);
        try {
            userService.getUserDetailsById(userId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Batch fetch user details by IDs
     * Called by other microservices to get multiple users at once (e.g., for reporting)
     */
    @PostMapping("/batch")
    @Operation(summary = "Batch get users (Internal)", description = "Get multiple users by IDs for inter-service communication")
    public List<UserDetailsDTO> getUsersByIds(@RequestBody List<Long> userIds) {
        logger.info("INTERNAL POST /api/internal/users/batch - Count: {}", userIds.size());
        return userService.getUserDetailsByIds(userIds);
    }

    /**
     * Get total users count
     * Called by reporting service for dashboard statistics
     */
    @GetMapping("/count")
    @Operation(summary = "Get total users count (Internal)", description = "Get total count of all users")
    public Long getTotalUsers() {
        logger.info("INTERNAL GET /api/internal/users/count");
        return userService.getTotalUsersCount();
    }

    /**
     * Get active users count
     * Called by reporting service for dashboard statistics
     */
    @GetMapping("/active/count")
    @Operation(summary = "Get active users count (Internal)", description = "Get count of active users")
    public Long getActiveUsersCount() {
        logger.info("INTERNAL GET /api/internal/users/active/count");
        return userService.getActiveUsersCount();
    }

    /**
     * Get users count by role
     * Called by reporting service for dashboard statistics
     */
    @GetMapping("/role/{role}/count")
    @Operation(summary = "Get users count by role (Internal)", description = "Get count of users by role")
    public Long getUsersByRoleCount(@PathVariable String role) {
        logger.info("INTERNAL GET /api/internal/users/role/{}/count", role);
        return userService.getUsersByRoleCount(role);
    }

    /**
     * Get user statistics
     * Called by reporting service for dashboard
     */
    @GetMapping("/statistics")
    @Operation(summary = "Get user statistics (Internal)", description = "Get user statistics for reporting")
    public java.util.Map<String, Object> getUserStatistics() {
        logger.info("INTERNAL GET /api/internal/users/statistics");
        return userService.getUserStatistics();
    }

    /**
     * Get all users
     * Called by reporting service
     */
    @GetMapping
    @Operation(summary = "Get all users (Internal)", description = "Get all users for reporting")
    public List<UserDetailsDTO> getAllUsers() {
        logger.info("INTERNAL GET /api/internal/users");
        return userService.getAllUsers();
    }
}
