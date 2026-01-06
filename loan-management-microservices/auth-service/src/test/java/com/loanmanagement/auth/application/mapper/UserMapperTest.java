package com.loanmanagement.auth.application.mapper;

import com.loanmanagement.auth.application.dto.request.CreateUserRequest;
import com.loanmanagement.auth.application.dto.request.RegisterRequest;
import com.loanmanagement.auth.application.dto.response.UserResponse;
import com.loanmanagement.auth.domain.model.RoleType;
import com.loanmanagement.auth.domain.model.User;
import com.loanmanagement.auth.domain.model.UserRole;
import com.loanmanagement.common.dto.UserDetailsDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for UserMapper
 * Target Coverage: 95%+
 * Follows monolithic test patterns with AssertJ
 */
@DisplayName("UserMapper Tests")
class UserMapperTest {

    private UserMapper userMapper;

    private User testUser;
    private RegisterRequest registerRequest;
    private CreateUserRequest createUserRequest;

    @BeforeEach
    void setUp() {
        userMapper = Mappers.getMapper(UserMapper.class);

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

        UserRole customerRole = UserRole.builder()
                .id(1L)
                .role(RoleType.CUSTOMER)
                .user(testUser)
                .build();

        UserRole adminRole = UserRole.builder()
                .id(2L)
                .role(RoleType.ADMIN)
                .user(testUser)
                .build();

        Set<UserRole> roles = new HashSet<>();
        roles.add(customerRole);
        roles.add(adminRole);
        testUser.setRoles(roles);

        // Setup register request
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("newuser");
        registerRequest.setEmail("newuser@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setFullName("New User");
        registerRequest.setPhoneNumber("+9876543210");

        // Setup create user request
        createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("createuser");
        createUserRequest.setEmail("create@example.com");
        createUserRequest.setPassword("password123");
        createUserRequest.setFullName("Create User");
        createUserRequest.setPhoneNumber("+1111111111");
        createUserRequest.setRoles(Set.of(RoleType.LOAN_OFFICER));
    }

    // ===================== TO RESPONSE TESTS =====================

    @Test
    @DisplayName("Should map user to user response")
    void toResponse_ShouldMapAllFields_WhenUserProvided() {
        // When
        UserResponse response = userMapper.toResponse(testUser);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(testUser.getId());
        assertThat(response.getUsername()).isEqualTo(testUser.getUsername());
        assertThat(response.getEmail()).isEqualTo(testUser.getEmail());
        assertThat(response.getFullName()).isEqualTo(testUser.getFullName());
        assertThat(response.getPhoneNumber()).isEqualTo(testUser.getPhoneNumber());
        assertThat(response.getIsActive()).isEqualTo(testUser.getIsActive());
        assertThat(response.getCreatedAt()).isEqualTo(testUser.getCreatedAt());
        assertThat(response.getUpdatedAt()).isEqualTo(testUser.getUpdatedAt());
    }

    @Test
    @DisplayName("Should map user roles to role types")
    void toResponse_ShouldMapRoles_WhenUserHasRoles() {
        // When
        UserResponse response = userMapper.toResponse(testUser);

        // Then
        assertThat(response.getRoles()).isNotNull();
        assertThat(response.getRoles()).hasSize(2);
        assertThat(response.getRoles()).containsExactlyInAnyOrder(RoleType.CUSTOMER, RoleType.ADMIN);
    }

    @Test
    @DisplayName("Should handle user with no roles")
    void toResponse_ShouldHandleEmptyRoles_WhenUserHasNoRoles() {
        // Given
        testUser.setRoles(new HashSet<>());

        // When
        UserResponse response = userMapper.toResponse(testUser);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getRoles()).isEmpty();
    }

    @Test
    @DisplayName("Should handle null user")
    void toResponse_ShouldReturnNull_WhenUserIsNull() {
        // When
        UserResponse response = userMapper.toResponse(null);

        // Then
        assertThat(response).isNull();
    }

    // ===================== TO USER DETAILS DTO TESTS =====================

    @Test
    @DisplayName("Should map user to UserDetailsDTO for inter-service communication")
    void toUserDetailsDTO_ShouldMapAllFields_WhenUserProvided() {
        // When
        UserDetailsDTO dto = userMapper.toUserDetailsDTO(testUser);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(testUser.getId());
        assertThat(dto.getUsername()).isEqualTo(testUser.getUsername());
        assertThat(dto.getEmail()).isEqualTo(testUser.getEmail());
        assertThat(dto.getFullName()).isEqualTo(testUser.getFullName());
        assertThat(dto.getPhoneNumber()).isEqualTo(testUser.getPhoneNumber());
        assertThat(dto.getIsActive()).isEqualTo(testUser.getIsActive());
    }

    @Test
    @DisplayName("Should map user roles to string set in UserDetailsDTO")
    void toUserDetailsDTO_ShouldMapRolesToStrings_WhenUserHasRoles() {
        // When
        UserDetailsDTO dto = userMapper.toUserDetailsDTO(testUser);

        // Then
        assertThat(dto.getRoles()).isNotNull();
        assertThat(dto.getRoles()).hasSize(2);
        assertThat(dto.getRoles()).containsExactlyInAnyOrder("CUSTOMER", "ADMIN");
    }

    @Test
    @DisplayName("Should handle user with no roles in UserDetailsDTO")
    void toUserDetailsDTO_ShouldHandleEmptyRoles_WhenUserHasNoRoles() {
        // Given
        testUser.setRoles(new HashSet<>());

        // When
        UserDetailsDTO dto = userMapper.toUserDetailsDTO(testUser);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getRoles()).isEmpty();
    }

    @Test
    @DisplayName("Should handle null user for UserDetailsDTO")
    void toUserDetailsDTO_ShouldReturnNull_WhenUserIsNull() {
        // When
        UserDetailsDTO dto = userMapper.toUserDetailsDTO(null);

        // Then
        assertThat(dto).isNull();
    }

    // ===================== TO ENTITY FROM REGISTER REQUEST TESTS =====================

    @Test
    @DisplayName("Should map RegisterRequest to User entity")
    void toEntity_ShouldMapAllFields_WhenRegisterRequestProvided() {
        // When
        User user = userMapper.toEntity(registerRequest);

        // Then
        assertThat(user).isNotNull();
        assertThat(user.getUsername()).isEqualTo(registerRequest.getUsername());
        assertThat(user.getEmail()).isEqualTo(registerRequest.getEmail());
        assertThat(user.getFullName()).isEqualTo(registerRequest.getFullName());
        assertThat(user.getPhoneNumber()).isEqualTo(registerRequest.getPhoneNumber());
    }

    @Test
    @DisplayName("Should not map password field from RegisterRequest")
    void toEntity_ShouldNotMapPassword_WhenRegisterRequestProvided() {
        // When
        User user = userMapper.toEntity(registerRequest);

        // Then
        assertThat(user).isNotNull();
        assertThat(user.getPasswordHash()).isNull(); // Password should be encoded separately
    }

    @Test
    @DisplayName("Should ignore ID when mapping from RegisterRequest")
    void toEntity_ShouldIgnoreId_WhenRegisterRequestProvided() {
        // When
        User user = userMapper.toEntity(registerRequest);

        // Then
        assertThat(user).isNotNull();
        assertThat(user.getId()).isNull(); // ID should not be set
    }

    @Test
    @DisplayName("Should ignore roles when mapping from RegisterRequest")
    void toEntity_ShouldIgnoreRoles_WhenRegisterRequestProvided() {
        // When
        User user = userMapper.toEntity(registerRequest);

        // Then
        assertThat(user).isNotNull();
        assertThat(user.getRoles()).isNotNull();
        assertThat(user.getRoles()).isEmpty(); // Roles should be assigned separately
    }

    @Test
    @DisplayName("Should handle null RegisterRequest")
    void toEntity_ShouldReturnNull_WhenRegisterRequestIsNull() {
        // When
        User user = userMapper.toEntity((RegisterRequest) null);

        // Then
        assertThat(user).isNull();
    }

    // ===================== TO ENTITY FROM CREATE USER REQUEST TESTS =====================

    @Test
    @DisplayName("Should map CreateUserRequest to User entity")
    void toEntity_ShouldMapAllFields_WhenCreateUserRequestProvided() {
        // When
        User user = userMapper.toEntity(createUserRequest);

        // Then
        assertThat(user).isNotNull();
        assertThat(user.getUsername()).isEqualTo(createUserRequest.getUsername());
        assertThat(user.getEmail()).isEqualTo(createUserRequest.getEmail());
        assertThat(user.getFullName()).isEqualTo(createUserRequest.getFullName());
        assertThat(user.getPhoneNumber()).isEqualTo(createUserRequest.getPhoneNumber());
    }

    @Test
    @DisplayName("Should ignore ID when mapping from CreateUserRequest")
    void toEntity_ShouldIgnoreId_WhenCreateUserRequestProvided() {
        // When
        User user = userMapper.toEntity(createUserRequest);

        // Then
        assertThat(user).isNotNull();
        assertThat(user.getId()).isNull(); // ID should not be set
    }

    @Test
    @DisplayName("Should ignore roles when mapping from CreateUserRequest")
    void toEntity_ShouldIgnoreRoles_WhenCreateUserRequestProvided() {
        // When
        User user = userMapper.toEntity(createUserRequest);

        // Then
        assertThat(user).isNotNull();
        assertThat(user.getRoles()).isNotNull();
        assertThat(user.getRoles()).isEmpty(); // Roles should be assigned separately
    }

    @Test
    @DisplayName("Should handle null CreateUserRequest")
    void toEntity_ShouldReturnNull_WhenCreateUserRequestIsNull() {
        // When
        User user = userMapper.toEntity((CreateUserRequest) null);

        // Then
        assertThat(user).isNull();
    }

    // ===================== ROLES MAPPING TESTS =====================

    @Test
    @DisplayName("Should map UserRole set to RoleType set")
    void rolesToRoleTypes_ShouldMapCorrectly_WhenRolesProvided() {
        // Given
        Set<UserRole> userRoles = testUser.getRoles();

        // When
        Set<RoleType> roleTypes = userMapper.rolesToRoleTypes(userRoles);

        // Then
        assertThat(roleTypes).isNotNull();
        assertThat(roleTypes).hasSize(2);
        assertThat(roleTypes).containsExactlyInAnyOrder(RoleType.CUSTOMER, RoleType.ADMIN);
    }

    @Test
    @DisplayName("Should return empty set when no roles provided")
    void rolesToRoleTypes_ShouldReturnEmpty_WhenNoRoles() {
        // Given
        Set<UserRole> emptyRoles = new HashSet<>();

        // When
        Set<RoleType> roleTypes = userMapper.rolesToRoleTypes(emptyRoles);

        // Then
        assertThat(roleTypes).isNotNull();
        assertThat(roleTypes).isEmpty();
    }

    @Test
    @DisplayName("Should map UserRole set to String set")
    void rolesToStrings_ShouldMapCorrectly_WhenRolesProvided() {
        // Given
        Set<UserRole> userRoles = testUser.getRoles();

        // When
        Set<String> roleStrings = userMapper.rolesToStrings(userRoles);

        // Then
        assertThat(roleStrings).isNotNull();
        assertThat(roleStrings).hasSize(2);
        assertThat(roleStrings).containsExactlyInAnyOrder("CUSTOMER", "ADMIN");
    }

    @Test
    @DisplayName("Should return empty set when no roles for string mapping")
    void rolesToStrings_ShouldReturnEmpty_WhenNoRoles() {
        // Given
        Set<UserRole> emptyRoles = new HashSet<>();

        // When
        Set<String> roleStrings = userMapper.rolesToStrings(emptyRoles);

        // Then
        assertThat(roleStrings).isNotNull();
        assertThat(roleStrings).isEmpty();
    }

    // ===================== SINGLE ROLE MAPPING TESTS =====================

    @Test
    @DisplayName("Should map user with single role correctly")
    void toResponse_ShouldMapCorrectly_WhenUserHasSingleRole() {
        // Given
        User singleRoleUser = new User();
        singleRoleUser.setId(2L);
        singleRoleUser.setUsername("singleuser");
        singleRoleUser.setEmail("single@example.com");
        singleRoleUser.setFullName("Single Role User");

        UserRole role = UserRole.builder()
                .role(RoleType.LOAN_OFFICER)
                .user(singleRoleUser)
                .build();
        singleRoleUser.setRoles(Set.of(role));

        // When
        UserResponse response = userMapper.toResponse(singleRoleUser);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getRoles()).hasSize(1);
        assertThat(response.getRoles()).containsExactly(RoleType.LOAN_OFFICER);
    }

    // ===================== EDGE CASES =====================

    @Test
    @DisplayName("Should handle user with special characters in fields")
    void toResponse_ShouldHandleSpecialCharacters() {
        // Given
        User specialUser = new User();
        specialUser.setId(3L);
        specialUser.setUsername("user.test+123");
        specialUser.setEmail("test+special@example.com");
        specialUser.setFullName("Test User (Special)");
        specialUser.setPhoneNumber("+1-234-567-8900");

        // When
        UserResponse response = userMapper.toResponse(specialUser);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getUsername()).isEqualTo(specialUser.getUsername());
        assertThat(response.getEmail()).isEqualTo(specialUser.getEmail());
        assertThat(response.getFullName()).isEqualTo(specialUser.getFullName());
        assertThat(response.getPhoneNumber()).isEqualTo(specialUser.getPhoneNumber());
    }

    @Test
    @DisplayName("Should handle minimal user data")
    void toResponse_ShouldHandleMinimalData_WhenOptionalFieldsNull() {
        // Given
        User minimalUser = new User();
        minimalUser.setId(4L);
        minimalUser.setUsername("minimal");
        minimalUser.setEmail("minimal@example.com");
        minimalUser.setPasswordHash("hash");
        minimalUser.setFullName("Minimal User");
        minimalUser.setIsActive(true);
        // Phone number is null
        minimalUser.setRoles(new HashSet<>());

        // When
        UserResponse response = userMapper.toResponse(minimalUser);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(4L);
        assertThat(response.getUsername()).isEqualTo("minimal");
        assertThat(response.getPhoneNumber()).isNull();
        assertThat(response.getRoles()).isEmpty();
    }
}
