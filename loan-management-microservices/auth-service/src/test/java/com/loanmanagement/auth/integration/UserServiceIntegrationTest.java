package com.loanmanagement.auth.integration;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.loanmanagement.auth.application.dto.request.CreateUserRequest;
import com.loanmanagement.auth.application.dto.request.UpdateUserRequest;
import com.loanmanagement.auth.application.dto.response.PageResponse;
import com.loanmanagement.auth.application.dto.response.UserResponse;
import com.loanmanagement.auth.domain.model.RoleType;
import com.loanmanagement.auth.domain.service.UserService;
import com.loanmanagement.common.exception.ResourceNotFoundException;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration tests for UserService
 * Tests user management operations with real Spring context and database
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("UserService Integration Tests")
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Test
    @DisplayName("Should create new user")
    void shouldCreateUser() {
        // Given
        CreateUserRequest request = CreateUserRequest.builder()
                .username("testuser123")
                .email("testuser@example.com")
                .fullName("Test User")
                .phoneNumber("9876543210")
                .password("SecurePass123!")
                .roles(Set.of(RoleType.CUSTOMER))
                .build();

        // When
        UserResponse response = userService.createUser(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isNotNull();
        assertThat(response.getUsername()).isEqualTo("testuser123");
        assertThat(response.getEmail()).isEqualTo("testuser@example.com");
        assertThat(response.getFullName()).isEqualTo("Test User");
        assertThat(response.getPhoneNumber()).isEqualTo("9876543210");
        assertThat(response.getIsActive()).isTrue();
        assertThat(response.getRoles()).contains(RoleType.CUSTOMER);
    }

    @Test
    @DisplayName("Should get all users paginated")
    void shouldGetAllUsersPaginated() {
        // Given - Create multiple users
        CreateUserRequest request1 = CreateUserRequest.builder()
                .username("user1")
                .email("user1@example.com")
                .fullName("User One")
                .phoneNumber("1111111111")
                .password("Password123!")
                .roles(Set.of(RoleType.CUSTOMER))
                .build();

        CreateUserRequest request2 = CreateUserRequest.builder()
                .username("user2")
                .email("user2@example.com")
                .fullName("User Two")
                .phoneNumber("2222222222")
                .password("Password123!")
                .roles(Set.of(RoleType.LOAN_OFFICER))
                .build();

        userService.createUser(request1);
        userService.createUser(request2);

        Pageable pageable = PageRequest.of(0, 10);

        // When
        PageResponse<UserResponse> response = userService.getAllUsers(pageable);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSizeGreaterThanOrEqualTo(2);
        assertThat(response.getTotalElements()).isGreaterThanOrEqualTo(2);
    }

    @Test
    @DisplayName("Should get user by ID")
    void shouldGetUserById() {
        // Given
        CreateUserRequest request = CreateUserRequest.builder()
                .username("findme")
                .email("findme@example.com")
                .fullName("Find Me")
                .phoneNumber("3333333333")
                .password("Password123!")
                .roles(Set.of(RoleType.CUSTOMER))
                .build();

        UserResponse created = userService.createUser(request);

        // When
        UserResponse found = userService.getUserById(created.getId());

        // Then
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(created.getId());
        assertThat(found.getUsername()).isEqualTo("findme");
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void shouldThrowExceptionWhenUserNotFound() {
        // When/Then
        assertThatThrownBy(() -> userService.getUserById(99999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    @DisplayName("Should update user")
    void shouldUpdateUser() {
        // Given
        CreateUserRequest createRequest = CreateUserRequest.builder()
                .username("updateme")
                .email("updateme@example.com")
                .fullName("Update Me")
                .phoneNumber("4444444444")
                .password("Password123!")
                .roles(Set.of(RoleType.CUSTOMER))
                .build();

        UserResponse created = userService.createUser(createRequest);

        UpdateUserRequest updateRequest = UpdateUserRequest.builder()
                .fullName("Updated Name")
                .email("updated@example.com")
                .phoneNumber("5555555555")
                .build();

        // When
        UserResponse updated = userService.updateUser(created.getId(), updateRequest);

        // Then
        assertThat(updated.getId()).isEqualTo(created.getId());
        assertThat(updated.getFullName()).isEqualTo("Updated Name");
        assertThat(updated.getEmail()).isEqualTo("updated@example.com");
        assertThat(updated.getPhoneNumber()).isEqualTo("5555555555");
    }

    @Test
    @DisplayName("Should deactivate user")
    void shouldDeactivateUser() {
        // Given
        CreateUserRequest request = CreateUserRequest.builder()
                .username("deactivateme")
                .email("deactivate@example.com")
                .fullName("Deactivate Me")
                .phoneNumber("6666666666")
                .password("Password123!")
                .roles(Set.of(RoleType.CUSTOMER))
                .build();

        UserResponse created = userService.createUser(request);

        // When
        userService.deactivateUser(created.getId());

        // Then
        UserResponse deactivated = userService.getUserById(created.getId());
        assertThat(deactivated.getIsActive()).isFalse();
    }

    @Test
    @DisplayName("Should activate user")
    void shouldActivateUser() {
        // Given
        CreateUserRequest request = CreateUserRequest.builder()
                .username("activateme")
                .email("activate@example.com")
                .fullName("Activate Me")
                .phoneNumber("7777777777")
                .password("Password123!")
                .roles(Set.of(RoleType.CUSTOMER))
                .build();

        UserResponse created = userService.createUser(request);
        userService.deactivateUser(created.getId());

        // When
        userService.activateUser(created.getId());

        // Then
        UserResponse activated = userService.getUserById(created.getId());
        assertThat(activated.getIsActive()).isTrue();
    }

    @Test
    @DisplayName("Should get all roles")
    void shouldGetAllRoles() {
        // When
        List<RoleType> roles = userService.getAllRoles();

        // Then
        assertThat(roles).isNotNull();
        assertThat(roles).contains(RoleType.ADMIN, RoleType.LOAN_OFFICER, RoleType.CUSTOMER);
        assertThat(roles).hasSize(3);
    }

    @Test
    @DisplayName("Should create user with multiple roles")
    void shouldCreateUserWithMultipleRoles() {
        // Given
        CreateUserRequest request = CreateUserRequest.builder()
                .username("multirole")
                .email("multirole@example.com")
                .fullName("Multi Role User")
                .phoneNumber("8888888888")
                .password("Password123!")
                .roles(Set.of(RoleType.LOAN_OFFICER, RoleType.CUSTOMER))
                .build();

        // When
        UserResponse response = userService.createUser(request);

        // Then
        assertThat(response.getRoles()).hasSize(2);
        assertThat(response.getRoles()).containsExactlyInAnyOrder(RoleType.LOAN_OFFICER, RoleType.CUSTOMER);
    }
}
