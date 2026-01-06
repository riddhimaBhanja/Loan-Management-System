package com.loanmanagement.auth.application.controller;

import com.loanmanagement.auth.application.dto.request.CreateUserRequest;
import com.loanmanagement.auth.application.dto.request.UpdateUserRequest;
import com.loanmanagement.auth.application.dto.request.UpdateUserRolesRequest;
import com.loanmanagement.auth.application.dto.response.ApiResponse;
import com.loanmanagement.auth.application.dto.response.PageResponse;
import com.loanmanagement.auth.application.dto.response.UserResponse;
import com.loanmanagement.auth.domain.service.UserService;
import com.loanmanagement.auth.domain.model.RoleType;
import com.loanmanagement.auth.shared.constants.ApiConstants;
import com.loanmanagement.auth.shared.constants.MessageConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for user management endpoints
 */
@RestController
@RequestMapping(ApiConstants.USER_BASE_PATH)
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "User Management", description = "User management APIs")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LOAN_OFFICER')")
    @Operation(summary = "Get all users", description = "Get paginated list of all users (Admin/Loan Officer only)")
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getAllUsers(
            @RequestParam(defaultValue = ApiConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = ApiConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        logger.info("GET /api/users - page: {}, size: {}", page, size);

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        PageResponse<UserResponse> response = userService.getAllUsers(pageable);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LOAN_OFFICER') or @userService.isCurrentUser(#userId)")
    @Operation(summary = "Get user by ID", description = "Get user details by ID")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long userId) {
        logger.info("GET /api/users/{}", userId);

        UserResponse response = userService.getUserById(userId);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Get current logged-in user details")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser() {
        logger.info("GET /api/users/me");

        UserResponse response = userService.getCurrentUser();

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create user", description = "Create new user (Admin only)")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody CreateUserRequest request) {
        logger.info("POST /api/users - Username: {}", request.getUsername());

        UserResponse response = userService.createUser(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(MessageConstants.USER_CREATED, response));
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or @userService.isCurrentUser(#userId)")
    @Operation(summary = "Update user", description = "Update user details")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserRequest request) {

        logger.info("PUT /api/users/{}", userId);

        UserResponse response = userService.updateUser(userId, request);

        return ResponseEntity.ok(ApiResponse.success(MessageConstants.USER_UPDATED, response));
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deactivate user", description = "Deactivate user account (Admin only)")
    public ResponseEntity<ApiResponse<Void>> deactivateUser(@PathVariable Long userId) {
        logger.info("DELETE /api/users/{}", userId);

        userService.deactivateUser(userId);

        return ResponseEntity.ok(ApiResponse.success(MessageConstants.USER_DELETED));
    }

    @PatchMapping("/{userId}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Activate user", description = "Activate user account (Admin only)")
    public ResponseEntity<ApiResponse<Void>> activateUser(@PathVariable Long userId) {
        logger.info("PATCH /api/users/{}/activate", userId);

        userService.activateUser(userId);

        return ResponseEntity.ok(ApiResponse.success("User activated successfully"));
    }

    @PutMapping("/{userId}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update user roles", description = "Update user role assignments (Admin only)")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserRoles(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserRolesRequest request) {

        logger.info("PUT /api/users/{}/roles", userId);

        UserResponse response = userService.updateUserRoles(userId, request);

        return ResponseEntity.ok(ApiResponse.success("User roles updated successfully", response));
    }

    @GetMapping("/roles")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all roles", description = "Get list of all available system roles (Admin only)")
    public ResponseEntity<ApiResponse<List<RoleType>>> getAllRoles() {
        logger.info("GET /api/users/roles");

        List<RoleType> roles = userService.getAllRoles();

        return ResponseEntity.ok(ApiResponse.success(roles));
    }
}
