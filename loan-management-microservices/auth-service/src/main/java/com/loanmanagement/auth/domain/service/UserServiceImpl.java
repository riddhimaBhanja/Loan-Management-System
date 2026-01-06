package com.loanmanagement.auth.domain.service;

import com.loanmanagement.auth.application.dto.request.CreateUserRequest;
import com.loanmanagement.auth.application.dto.request.UpdateUserRequest;
import com.loanmanagement.auth.application.dto.request.UpdateUserRolesRequest;
import com.loanmanagement.auth.application.dto.response.PageResponse;
import com.loanmanagement.auth.application.dto.response.UserResponse;
import com.loanmanagement.auth.application.mapper.UserMapper;
import com.loanmanagement.auth.domain.model.User;
import com.loanmanagement.auth.domain.model.UserRole;
import com.loanmanagement.auth.domain.repository.UserRepository;
import com.loanmanagement.auth.domain.model.RoleType;
import com.loanmanagement.auth.shared.constants.MessageConstants;
import com.loanmanagement.common.dto.UserDetailsDTO;
import com.loanmanagement.common.exception.BusinessException;
import com.loanmanagement.common.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of UserService
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UserResponse> getAllUsers(Pageable pageable) {
        logger.info("Fetching all users, page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());

        Page<User> userPage = userRepository.findAll(pageable);

        return PageResponse.from(
                userPage,
                userPage.getContent().stream()
                        .map(userMapper::toResponse)
                        .collect(Collectors.toList())
        );
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long userId) {
        logger.info("Fetching user by ID: {}", userId);

        User user = userRepository.findByIdWithRoles(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        MessageConstants.USER_NOT_FOUND
                ));

        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser() {
        String username = getCurrentUsername();
        logger.info("Fetching current user: {}", username);

        User user = userRepository.findByUsernameWithRoles(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        MessageConstants.USER_NOT_FOUND
                ));

        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse createUser(CreateUserRequest request) {
        logger.info("Creating new user: {}", request.getUsername());

        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException(MessageConstants.USER_ALREADY_EXISTS);
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(MessageConstants.USER_ALREADY_EXISTS);
        }

        // Map request to entity
        User user = userMapper.toEntity(request);

        // Hash password
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        // Assign roles
        request.getRoles().forEach(roleType -> {
            UserRole role = UserRole.builder()
                    .role(roleType)
                    .build();
            user.addRole(role);
        });

        // Save user
        User savedUser = userRepository.save(user);

        logger.info("User created successfully: {}", savedUser.getUsername());

        return userMapper.toResponse(savedUser);
    }

    @Override
    public UserResponse updateUser(Long userId, UpdateUserRequest request) {
        logger.info("Updating user ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        MessageConstants.USER_NOT_FOUND
                ));

        // Update email if provided
        if (StringUtils.hasText(request.getEmail())) {
            // Check if email is already used by another user
            if (!request.getEmail().equals(user.getEmail()) &&
                    userRepository.existsByEmail(request.getEmail())) {
                throw new BusinessException("Email already in use");
            }
            user.setEmail(request.getEmail());
        }

        // Update full name if provided
        if (StringUtils.hasText(request.getFullName())) {
            user.setFullName(request.getFullName());
        }

        // Update phone number if provided
        if (StringUtils.hasText(request.getPhoneNumber())) {
            user.setPhoneNumber(request.getPhoneNumber());
        }

        // Update password if provided
        if (StringUtils.hasText(request.getPassword())) {
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }

        User updatedUser = userRepository.save(user);

        logger.info("User updated successfully: {}", updatedUser.getUsername());

        return userMapper.toResponse(updatedUser);
    }

    @Override
    public void deactivateUser(Long userId) {
        logger.info("Deactivating user ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        MessageConstants.USER_NOT_FOUND
                ));

        user.setIsActive(false);
        userRepository.save(user);

        logger.info("User deactivated: {}", user.getUsername());
    }

    @Override
    public void activateUser(Long userId) {
        logger.info("Activating user ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        MessageConstants.USER_NOT_FOUND
                ));

        user.setIsActive(true);
        userRepository.save(user);

        logger.info("User activated: {}", user.getUsername());
    }

    @Override
    public UserResponse updateUserRoles(Long userId, UpdateUserRolesRequest request) {
        logger.info("Updating roles for user ID: {}", userId);

        User user = userRepository.findByIdWithRoles(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        MessageConstants.USER_NOT_FOUND
                ));

        // Remove all existing roles
        user.getRoles().clear();

        // Add new roles
        request.getRoles().forEach(roleType -> {
            UserRole role = UserRole.builder()
                    .role(roleType)
                    .build();
            user.addRole(role);
        });

        User updatedUser = userRepository.save(user);

        logger.info("User roles updated successfully for: {}", updatedUser.getUsername());

        return userMapper.toResponse(updatedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleType> getAllRoles() {
        logger.info("Fetching all available roles");
        return Arrays.asList(RoleType.values());
    }

    @Override
    public boolean isCurrentUser(Long userId) {
        try {
            String currentUsername = getCurrentUsername();
            User currentUser = userRepository.findByUsername(currentUsername)
                    .orElse(null);
            return currentUser != null && currentUser.getId().equals(userId);
        } catch (Exception e) {
            return false;
        }
    }

    // ========== Inter-Service Communication Methods ==========

    @Override
    @Transactional(readOnly = true)
    public UserDetailsDTO getUserDetailsById(Long userId) {
        logger.info("Fetching user details by ID for inter-service communication: {}", userId);

        User user = userRepository.findByIdWithRoles(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        MessageConstants.USER_NOT_FOUND
                ));

        return userMapper.toUserDetailsDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetailsDTO getUserDetailsByUsername(String username) {
        logger.info("Fetching user details by username for inter-service communication: {}", username);

        User user = userRepository.findByUsernameWithRoles(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        MessageConstants.USER_NOT_FOUND
                ));

        return userMapper.toUserDetailsDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDetailsDTO> getUserDetailsByIds(List<Long> userIds) {
        logger.info("Batch fetching user details for {} users", userIds.size());

        List<User> users = userRepository.findByIdInWithRoles(userIds);

        return users.stream()
                .map(userMapper::toUserDetailsDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Long getTotalUsersCount() {
        return userRepository.count();
    }

    @Override
    public Long getActiveUsersCount() {
        return userRepository.countByIsActive(true);
    }

    @Override
    public Long getUsersByRoleCount(String role) {
        try {
            RoleType roleType = RoleType.valueOf(role);
            return userRepository.countByRolesContaining(roleType);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid role type: {}", role);
            return 0L;
        }
    }

    @Override
    public java.util.Map<String, Object> getUserStatistics() {
        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("totalUsers", getTotalUsersCount());
        stats.put("activeUsers", getActiveUsersCount());
        stats.put("adminUsers", getUsersByRoleCount("ADMIN"));
        stats.put("loanOfficers", getUsersByRoleCount("LOAN_OFFICER"));
        stats.put("customers", getUsersByRoleCount("CUSTOMER"));
        return stats;
    }

    @Override
    public List<UserDetailsDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(userMapper::toUserDetailsDTO)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Helper method to get current username from SecurityContext
     */
    private String getCurrentUsername() {
        try {
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null) {
                logger.error("No authentication found in SecurityContext");
                throw new BusinessException("User not authenticated");
            }

            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                return ((UserDetails) principal).getUsername();
            }
            return principal.toString();
        } catch (Exception e) {
            logger.error("Error getting current username", e);
            throw new BusinessException("Unable to get current user: " + e.getMessage());
        }
    }
}
