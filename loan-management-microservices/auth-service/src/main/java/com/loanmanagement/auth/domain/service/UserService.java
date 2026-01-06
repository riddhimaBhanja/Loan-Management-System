package com.loanmanagement.auth.domain.service;

import com.loanmanagement.auth.application.dto.request.CreateUserRequest;
import com.loanmanagement.auth.application.dto.request.UpdateUserRequest;
import com.loanmanagement.auth.application.dto.request.UpdateUserRolesRequest;
import com.loanmanagement.auth.application.dto.response.PageResponse;
import com.loanmanagement.auth.application.dto.response.UserResponse;
import com.loanmanagement.auth.domain.model.RoleType;
import com.loanmanagement.common.dto.UserDetailsDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for user management operations
 */
public interface UserService {

    /**
     * Get all users (paginated)
     */
    PageResponse<UserResponse> getAllUsers(Pageable pageable);

    /**
     * Get user by ID
     */
    UserResponse getUserById(Long userId);

    /**
     * Get current logged-in user
     */
    UserResponse getCurrentUser();

    /**
     * Create new user (Admin/Loan Officer)
     */
    UserResponse createUser(CreateUserRequest request);

    /**
     * Update user
     */
    UserResponse updateUser(Long userId, UpdateUserRequest request);

    /**
     * Update user roles (Admin only)
     */
    UserResponse updateUserRoles(Long userId, UpdateUserRolesRequest request);

    /**
     * Deactivate user
     */
    void deactivateUser(Long userId);

    /**
     * Activate user
     */
    void activateUser(Long userId);

    /**
     * Get all available roles
     */
    List<RoleType> getAllRoles();

    /**
     * Check if user ID matches current user
     */
    boolean isCurrentUser(Long userId);

    /**
     * Get user details for inter-service communication
     */
    UserDetailsDTO getUserDetailsById(Long userId);

    /**
     * Get user details by username for inter-service communication
     */
    UserDetailsDTO getUserDetailsByUsername(String username);

    /**
     * Get multiple users details for inter-service communication
     */
    List<UserDetailsDTO> getUserDetailsByIds(List<Long> userIds);

    /**
     * Get total users count for reporting
     */
    Long getTotalUsersCount();

    /**
     * Get active users count for reporting
     */
    Long getActiveUsersCount();

    /**
     * Get users count by role for reporting
     */
    Long getUsersByRoleCount(String role);

    /**
     * Get user statistics for reporting
     */
    java.util.Map<String, Object> getUserStatistics();

    /**
     * Get all users for reporting (no pagination)
     */
    List<UserDetailsDTO> getAllUsers();
}
