package com.loanmanagement.auth.domain.service;

import com.loanmanagement.auth.application.dto.request.CreateUserRequest;
import com.loanmanagement.auth.application.dto.request.UpdateUserRequest;
import com.loanmanagement.auth.application.dto.request.UpdateUserRolesRequest;
import com.loanmanagement.auth.application.dto.response.PageResponse;
import com.loanmanagement.auth.application.dto.response.UserResponse;
import com.loanmanagement.auth.application.mapper.UserMapper;
import com.loanmanagement.auth.domain.model.RoleType;
import com.loanmanagement.auth.domain.model.User;
import com.loanmanagement.auth.domain.model.UserRole;
import com.loanmanagement.auth.domain.repository.UserRepository;
import com.loanmanagement.auth.shared.constants.MessageConstants;
import com.loanmanagement.common.dto.UserDetailsDTO;
import com.loanmanagement.common.exception.BusinessException;
import com.loanmanagement.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserServiceImpl
 * Target Coverage: 95%+
 * Follows monolithic test patterns with Mockito, JUnit 5, and AssertJ
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Tests")
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UserResponse userResponse;
    private CreateUserRequest createUserRequest;
    private UpdateUserRequest updateUserRequest;
    private UpdateUserRolesRequest updateUserRolesRequest;

    @BeforeEach
    void setUp() {
        // Setup test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPasswordHash("$2a$10$encoded");
        testUser.setFullName("Test User");
        testUser.setPhoneNumber("+1234567890");
        testUser.setIsActive(true);
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());

        UserRole customerRole = new UserRole();
        customerRole.setId(1L);
        customerRole.setRole(RoleType.CUSTOMER);
        customerRole.setUser(testUser);

        Set<UserRole> roles = new HashSet<>();
        roles.add(customerRole);
        testUser.setRoles(roles);

        // Setup user response
        userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setUsername("testuser");
        userResponse.setEmail("test@example.com");
        userResponse.setFullName("Test User");

        // Setup create user request
        createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("newuser");
        createUserRequest.setEmail("newuser@example.com");
        createUserRequest.setPassword("password123");
        createUserRequest.setFullName("New User");
        createUserRequest.setPhoneNumber("+9876543210");
        createUserRequest.setRoles(Set.of(RoleType.ADMIN));

        // Setup update user request
        updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setEmail("updated@example.com");
        updateUserRequest.setFullName("Updated Name");
        updateUserRequest.setPhoneNumber("+1111111111");
        updateUserRequest.setPassword("newPassword123");

        // Setup update user roles request
        updateUserRolesRequest = new UpdateUserRolesRequest();
        updateUserRolesRequest.setRoles(Set.of(RoleType.ADMIN, RoleType.LOAN_OFFICER));
    }

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    // ===================== GET ALL USERS =====================

    @Test
    @DisplayName("Should get all users successfully")
    void getAllUsers_ShouldReturnPagedUsers_WhenUsersExist() {
        // Given
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        Page<User> userPage = new PageImpl<>(List.of(testUser), pageable, 1);

        when(userRepository.findAll(pageable)).thenReturn(userPage);
        when(userMapper.toResponse(any(User.class))).thenReturn(userResponse);

        // When
        PageResponse<UserResponse> response = userService.getAllUsers(pageable);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getTotalElements()).isEqualTo(1);
        assertThat(response.getTotalPages()).isEqualTo(1);
        assertThat(response.getCurrentPage()).isEqualTo(0);

        verify(userRepository).findAll(pageable);
        verify(userMapper).toResponse(testUser);
    }

    @Test
    @DisplayName("Should return empty page when no users exist")
    void getAllUsers_ShouldReturnEmptyPage_WhenNoUsersExist() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(userRepository.findAll(pageable)).thenReturn(emptyPage);

        // When
        PageResponse<UserResponse> response = userService.getAllUsers(pageable);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getContent()).isEmpty();
        assertThat(response.getTotalElements()).isEqualTo(0);

        verify(userRepository).findAll(pageable);
        verify(userMapper, never()).toResponse(any());
    }

    // ===================== GET USER BY ID =====================

    @Test
    @DisplayName("Should get user by ID successfully")
    void getUserById_ShouldReturnUser_WhenUserExists() {
        // Given
        when(userRepository.findByIdWithRoles(1L)).thenReturn(Optional.of(testUser));
        when(userMapper.toResponse(testUser)).thenReturn(userResponse);

        // When
        UserResponse response = userService.getUserById(1L);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getUsername()).isEqualTo("testuser");

        verify(userRepository).findByIdWithRoles(1L);
        verify(userMapper).toResponse(testUser);
    }

    @Test
    @DisplayName("Should throw exception when user not found by ID")
    void getUserById_ShouldThrowException_WhenUserNotFound() {
        // Given
        when(userRepository.findByIdWithRoles(999L)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> userService.getUserById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(MessageConstants.USER_NOT_FOUND);

        verify(userRepository).findByIdWithRoles(999L);
        verify(userMapper, never()).toResponse(any());
    }

    // ===================== GET CURRENT USER =====================

    @Test
    @DisplayName("Should get current user successfully")
    void getCurrentUser_ShouldReturnUser_WhenAuthenticated() {
        // Given
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("testuser", "password")
        );

        when(userRepository.findByUsernameWithRoles("testuser")).thenReturn(Optional.of(testUser));
        when(userMapper.toResponse(testUser)).thenReturn(userResponse);

        // When
        UserResponse response = userService.getCurrentUser();

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getUsername()).isEqualTo("testuser");

        verify(userRepository).findByUsernameWithRoles("testuser");
        verify(userMapper).toResponse(testUser);
    }

    @Test
    @DisplayName("Should throw exception when current user not found")
    void getCurrentUser_ShouldThrowException_WhenUserNotFound() {
        // Given
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("nonexistent", "password")
        );

        when(userRepository.findByUsernameWithRoles("nonexistent")).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> userService.getCurrentUser())
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(MessageConstants.USER_NOT_FOUND);

        verify(userRepository).findByUsernameWithRoles("nonexistent");
    }

    // ===================== CREATE USER =====================

    @Test
    @DisplayName("Should create user successfully")
    void createUser_ShouldReturnCreatedUser_WhenValidRequest() {
        // Given
        User newUser = new User();
        newUser.setUsername("newuser");

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$encoded");
        when(userMapper.toEntity(createUserRequest)).thenReturn(newUser);
        when(userRepository.save(any(User.class))).thenReturn(newUser);
        when(userMapper.toResponse(newUser)).thenReturn(userResponse);

        // When
        UserResponse response = userService.createUser(createUserRequest);

        // Then
        assertThat(response).isNotNull();

        verify(userRepository).existsByUsername("newuser");
        verify(userRepository).existsByEmail("newuser@example.com");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when creating user with existing username")
    void createUser_ShouldThrowException_WhenUsernameExists() {
        // Given
        when(userRepository.existsByUsername("newuser")).thenReturn(true);

        // When / Then
        assertThatThrownBy(() -> userService.createUser(createUserRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(MessageConstants.USER_ALREADY_EXISTS);

        verify(userRepository).existsByUsername("newuser");
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when creating user with existing email")
    void createUser_ShouldThrowException_WhenEmailExists() {
        // Given
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(true);

        // When / Then
        assertThatThrownBy(() -> userService.createUser(createUserRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(MessageConstants.USER_ALREADY_EXISTS);

        verify(userRepository).existsByEmail("newuser@example.com");
        verify(userRepository, never()).save(any());
    }

    // ===================== UPDATE USER =====================

    @Test
    @DisplayName("Should update user successfully")
    void updateUser_ShouldReturnUpdatedUser_WhenValidRequest() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail("updated@example.com")).thenReturn(false);
        when(passwordEncoder.encode("newPassword123")).thenReturn("$2a$10$newEncoded");
        when(userRepository.save(testUser)).thenReturn(testUser);
        when(userMapper.toResponse(testUser)).thenReturn(userResponse);

        // When
        UserResponse response = userService.updateUser(1L, updateUserRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(testUser.getEmail()).isEqualTo("updated@example.com");
        assertThat(testUser.getFullName()).isEqualTo("Updated Name");
        assertThat(testUser.getPhoneNumber()).isEqualTo("+1111111111");

        verify(userRepository).findById(1L);
        verify(userRepository).save(testUser);
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent user")
    void updateUser_ShouldThrowException_WhenUserNotFound() {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> userService.updateUser(999L, updateUserRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(MessageConstants.USER_NOT_FOUND);

        verify(userRepository).findById(999L);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when updating email to existing one")
    void updateUser_ShouldThrowException_WhenEmailAlreadyInUse() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail("updated@example.com")).thenReturn(true);

        // When / Then
        assertThatThrownBy(() -> userService.updateUser(1L, updateUserRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Email already in use");

        verify(userRepository).findById(1L);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should allow updating to same email")
    void updateUser_ShouldAllowSameEmail_WhenEmailUnchanged() {
        // Given
        updateUserRequest.setEmail("test@example.com"); // Same as current
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(testUser)).thenReturn(testUser);
        when(userMapper.toResponse(testUser)).thenReturn(userResponse);

        // When
        UserResponse response = userService.updateUser(1L, updateUserRequest);

        // Then
        assertThat(response).isNotNull();
        verify(userRepository, never()).existsByEmail(anyString());
    }

    // ===================== ACTIVATE / DEACTIVATE =====================

    @Test
    @DisplayName("Should deactivate user successfully")
    void deactivateUser_ShouldSetInactive_WhenUserExists() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(testUser)).thenReturn(testUser);

        // When
        userService.deactivateUser(1L);

        // Then
        assertThat(testUser.getIsActive()).isFalse();
        verify(userRepository).findById(1L);
        verify(userRepository).save(testUser);
    }

    @Test
    @DisplayName("Should throw exception when deactivating non-existent user")
    void deactivateUser_ShouldThrowException_WhenUserNotFound() {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> userService.deactivateUser(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(MessageConstants.USER_NOT_FOUND);

        verify(userRepository).findById(999L);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should activate user successfully")
    void activateUser_ShouldSetActive_WhenUserExists() {
        // Given
        testUser.setIsActive(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(testUser)).thenReturn(testUser);

        // When
        userService.activateUser(1L);

        // Then
        assertThat(testUser.getIsActive()).isTrue();
        verify(userRepository).findById(1L);
        verify(userRepository).save(testUser);
    }

    @Test
    @DisplayName("Should throw exception when activating non-existent user")
    void activateUser_ShouldThrowException_WhenUserNotFound() {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> userService.activateUser(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(MessageConstants.USER_NOT_FOUND);

        verify(userRepository).findById(999L);
        verify(userRepository, never()).save(any());
    }

    // ===================== UPDATE ROLES =====================

    @Test
    @DisplayName("Should update user roles successfully")
    void updateUserRoles_ShouldReplaceRoles_WhenValidRequest() {
        // Given
        when(userRepository.findByIdWithRoles(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(testUser)).thenReturn(testUser);
        when(userMapper.toResponse(testUser)).thenReturn(userResponse);

        // When
        UserResponse response = userService.updateUserRoles(1L, updateUserRolesRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(testUser.getRoles()).hasSize(2);
        assertThat(testUser.getRoles().stream()
                .map(UserRole::getRole))
                .containsExactlyInAnyOrder(RoleType.ADMIN, RoleType.LOAN_OFFICER);

        verify(userRepository).findByIdWithRoles(1L);
        verify(userRepository).save(testUser);
    }

    @Test
    @DisplayName("Should throw exception when updating roles for non-existent user")
    void updateUserRoles_ShouldThrowException_WhenUserNotFound() {
        // Given
        when(userRepository.findByIdWithRoles(999L)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> userService.updateUserRoles(999L, updateUserRolesRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(MessageConstants.USER_NOT_FOUND);

        verify(userRepository).findByIdWithRoles(999L);
        verify(userRepository, never()).save(any());
    }

    // ===================== GET ALL ROLES =====================

    @Test
    @DisplayName("Should get all available roles")
    void getAllRoles_ShouldReturnAllRoleTypes() {
        // When
        List<RoleType> roles = userService.getAllRoles();

        // Then
        assertThat(roles).isNotEmpty();
        assertThat(roles).contains(RoleType.ADMIN, RoleType.CUSTOMER, RoleType.LOAN_OFFICER);
        assertThat(roles).hasSize(RoleType.values().length);
    }

    // ===================== IS CURRENT USER =====================

    @Test
    @DisplayName("Should return true when checking current user")
    void isCurrentUser_ShouldReturnTrue_WhenUserIsCurrentUser() {
        // Given
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("testuser", "password")
        );

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // When
        boolean result = userService.isCurrentUser(1L);

        // Then
        assertThat(result).isTrue();
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    @DisplayName("Should return false when checking different user")
    void isCurrentUser_ShouldReturnFalse_WhenUserIsDifferent() {
        // Given
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("testuser", "password")
        );

        User differentUser = new User();
        differentUser.setId(2L);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(differentUser));

        // When
        boolean result = userService.isCurrentUser(1L);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should return false when exception occurs")
    void isCurrentUser_ShouldReturnFalse_WhenExceptionOccurs() {
        // Given
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("testuser", "password")
        );

        when(userRepository.findByUsername("testuser")).thenThrow(new RuntimeException("Database error"));

        // When
        boolean result = userService.isCurrentUser(1L);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should return false when no authentication")
    void isCurrentUser_ShouldReturnFalse_WhenNoAuthentication() {
        // When
        boolean result = userService.isCurrentUser(1L);

        // Then
        assertThat(result).isFalse();
    }

    // ===================== INTER-SERVICE COMMUNICATION =====================

    @Test
    @DisplayName("Should get user details by ID for inter-service communication")
    void getUserDetailsById_ShouldReturnUserDetailsDTO_WhenUserExists() {
        // Given
        UserDetailsDTO userDetailsDTO = new UserDetailsDTO();
        userDetailsDTO.setId(1L);
        userDetailsDTO.setUsername("testuser");
        userDetailsDTO.setEmail("test@example.com");

        when(userRepository.findByIdWithRoles(1L)).thenReturn(Optional.of(testUser));
        when(userMapper.toUserDetailsDTO(testUser)).thenReturn(userDetailsDTO);

        // When
        UserDetailsDTO result = userService.getUserDetailsById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUsername()).isEqualTo("testuser");

        verify(userRepository).findByIdWithRoles(1L);
        verify(userMapper).toUserDetailsDTO(testUser);
    }

    @Test
    @DisplayName("Should throw exception when getting user details for non-existent user")
    void getUserDetailsById_ShouldThrowException_WhenUserNotFound() {
        // Given
        when(userRepository.findByIdWithRoles(999L)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> userService.getUserDetailsById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(MessageConstants.USER_NOT_FOUND);

        verify(userRepository).findByIdWithRoles(999L);
    }

    @Test
    @DisplayName("Should get user details by IDs for batch operations")
    void getUserDetailsByIds_ShouldReturnUserDetailsList_WhenUsersExist() {
        // Given
        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");

        List<Long> userIds = List.of(1L, 2L);
        List<User> users = List.of(testUser, user2);

        UserDetailsDTO dto1 = new UserDetailsDTO();
        dto1.setId(1L);
        UserDetailsDTO dto2 = new UserDetailsDTO();
        dto2.setId(2L);

        when(userRepository.findByIdInWithRoles(userIds)).thenReturn(users);
        when(userMapper.toUserDetailsDTO(testUser)).thenReturn(dto1);
        when(userMapper.toUserDetailsDTO(user2)).thenReturn(dto2);

        // When
        List<UserDetailsDTO> result = userService.getUserDetailsByIds(userIds);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(UserDetailsDTO::getId).containsExactly(1L, 2L);

        verify(userRepository).findByIdInWithRoles(userIds);
        verify(userMapper, times(2)).toUserDetailsDTO(any(User.class));
    }

    @Test
    @DisplayName("Should return empty list when batch fetching with no IDs")
    void getUserDetailsByIds_ShouldReturnEmptyList_WhenNoUserIds() {
        // Given
        List<Long> emptyIds = Collections.emptyList();
        when(userRepository.findByIdInWithRoles(emptyIds)).thenReturn(Collections.emptyList());

        // When
        List<UserDetailsDTO> result = userService.getUserDetailsByIds(emptyIds);

        // Then
        assertThat(result).isEmpty();
        verify(userRepository).findByIdInWithRoles(emptyIds);
    }
}
