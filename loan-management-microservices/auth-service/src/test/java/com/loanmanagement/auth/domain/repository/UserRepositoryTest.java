package com.loanmanagement.auth.domain.repository;

import com.loanmanagement.auth.domain.model.RoleType;
import com.loanmanagement.auth.domain.model.User;
import com.loanmanagement.auth.domain.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * Repository tests for UserRepository
 * Uses @DataJpaTest for repository layer testing
 * Target Coverage: 95%+
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("UserRepository Tests")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User testUser1;
    private User testUser2;

    @BeforeEach
    void setUp() {
        // Create test user 1
        testUser1 = new User();
        testUser1.setUsername("user1");
        testUser1.setEmail("user1@example.com");
        testUser1.setPasswordHash("$2a$10$encoded1");
        testUser1.setFullName("User One");
        testUser1.setPhoneNumber("+1111111111");
        testUser1.setIsActive(true);

        UserRole role1 = UserRole.builder()
                .role(RoleType.CUSTOMER)
                .build();
        testUser1.addRole(role1);

        // Create test user 2
        testUser2 = new User();
        testUser2.setUsername("user2");
        testUser2.setEmail("user2@example.com");
        testUser2.setPasswordHash("$2a$10$encoded2");
        testUser2.setFullName("User Two");
        testUser2.setPhoneNumber("+2222222222");
        testUser2.setIsActive(true);

        UserRole role2Admin = UserRole.builder()
                .role(RoleType.ADMIN)
                .build();
        UserRole role2Officer = UserRole.builder()
                .role(RoleType.LOAN_OFFICER)
                .build();
        testUser2.addRole(role2Admin);
        testUser2.addRole(role2Officer);

        // Persist users
        entityManager.persist(testUser1);
        entityManager.persist(testUser2);
        entityManager.flush();
    }

    // ===================== FIND BY USERNAME =====================

    @Test
    @DisplayName("Should find user by username")
    void findByUsername_ShouldReturnUser_WhenUsernameExists() {
        // When
        Optional<User> result = userRepository.findByUsername("user1");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("user1");
        assertThat(result.get().getEmail()).isEqualTo("user1@example.com");
    }

    @Test
    @DisplayName("Should return empty when username does not exist")
    void findByUsername_ShouldReturnEmpty_WhenUsernameNotExists() {
        // When
        Optional<User> result = userRepository.findByUsername("nonexistent");

        // Then
        assertThat(result).isEmpty();
    }

    // ===================== FIND BY EMAIL =====================

    @Test
    @DisplayName("Should find user by email")
    void findByEmail_ShouldReturnUser_WhenEmailExists() {
        // When
        Optional<User> result = userRepository.findByEmail("user1@example.com");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("user1@example.com");
        assertThat(result.get().getUsername()).isEqualTo("user1");
    }

    @Test
    @DisplayName("Should return empty when email does not exist")
    void findByEmail_ShouldReturnEmpty_WhenEmailNotExists() {
        // When
        Optional<User> result = userRepository.findByEmail("nonexistent@example.com");

        // Then
        assertThat(result).isEmpty();
    }

    // ===================== EXISTS BY USERNAME =====================

    @Test
    @DisplayName("Should return true when username exists")
    void existsByUsername_ShouldReturnTrue_WhenUsernameExists() {
        // When
        boolean exists = userRepository.existsByUsername("user1");

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false when username does not exist")
    void existsByUsername_ShouldReturnFalse_WhenUsernameNotExists() {
        // When
        boolean exists = userRepository.existsByUsername("nonexistent");

        // Then
        assertThat(exists).isFalse();
    }

    // ===================== EXISTS BY EMAIL =====================

    @Test
    @DisplayName("Should return true when email exists")
    void existsByEmail_ShouldReturnTrue_WhenEmailExists() {
        // When
        boolean exists = userRepository.existsByEmail("user1@example.com");

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false when email does not exist")
    void existsByEmail_ShouldReturnFalse_WhenEmailNotExists() {
        // When
        boolean exists = userRepository.existsByEmail("nonexistent@example.com");

        // Then
        assertThat(exists).isFalse();
    }

    // ===================== FIND BY ACTIVE STATUS =====================

    @Test
    @DisplayName("Should find active users")
    void findByIsActive_ShouldReturnActiveUsers_WhenActiveIsTrue() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<User> result = userRepository.findByIsActive(true, pageable);

        // Then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).allMatch(User::getIsActive);
    }

    @Test
    @DisplayName("Should find inactive users")
    void findByIsActive_ShouldReturnInactiveUsers_WhenActiveIsFalse() {
        // Given
        testUser1.setIsActive(false);
        entityManager.persist(testUser1);
        entityManager.flush();

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<User> result = userRepository.findByIsActive(false, pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getUsername()).isEqualTo("user1");
        assertThat(result.getContent().get(0).getIsActive()).isFalse();
    }

    // ===================== FIND BY ROLE =====================

    @Test
    @DisplayName("Should find users by role")
    void findByRole_ShouldReturnUsersWithRole_WhenRoleExists() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When - Find ADMIN users
        Page<User> adminUsers = userRepository.findByRole(RoleType.ADMIN, pageable);

        // Then
        assertThat(adminUsers.getContent()).hasSize(1);
        assertThat(adminUsers.getContent().get(0).getUsername()).isEqualTo("user2");
    }

    @Test
    @DisplayName("Should find users with CUSTOMER role")
    void findByRole_ShouldReturnCustomers_WhenRoleIsCustomer() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<User> customers = userRepository.findByRole(RoleType.CUSTOMER, pageable);

        // Then
        assertThat(customers.getContent()).hasSize(1);
        assertThat(customers.getContent().get(0).getUsername()).isEqualTo("user1");
    }

    @Test
    @DisplayName("Should return empty when no users have the role")
    void findByRole_ShouldReturnEmpty_WhenNoUsersHaveRole() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // Clear all users and create one without ADMIN role
        entityManager.clear();
        userRepository.deleteAll();

        User customerUser = new User();
        customerUser.setUsername("customer");
        customerUser.setEmail("customer@example.com");
        customerUser.setPasswordHash("$2a$10$encoded");
        customerUser.setFullName("Customer User");
        customerUser.setIsActive(true);

        UserRole customerRole = UserRole.builder().role(RoleType.CUSTOMER).build();
        customerUser.addRole(customerRole);

        entityManager.persist(customerUser);
        entityManager.flush();

        // When - Try to find ADMIN users
        Page<User> adminUsers = userRepository.findByRole(RoleType.ADMIN, pageable);

        // Then
        assertThat(adminUsers.getContent()).isEmpty();
    }

    // ===================== FIND BY USERNAME WITH ROLES =====================

    @Test
    @DisplayName("Should find user with roles by username")
    void findByUsernameWithRoles_ShouldReturnUserWithRoles_WhenUsernameExists() {
        // When
        Optional<User> result = userRepository.findByUsernameWithRoles("user2");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("user2");
        assertThat(result.get().getRoles()).hasSize(2);
        assertThat(result.get().getRoles())
                .extracting(UserRole::getRole)
                .containsExactlyInAnyOrder(RoleType.ADMIN, RoleType.LOAN_OFFICER);
    }

    @Test
    @DisplayName("Should return empty when username does not exist with roles query")
    void findByUsernameWithRoles_ShouldReturnEmpty_WhenUsernameNotExists() {
        // When
        Optional<User> result = userRepository.findByUsernameWithRoles("nonexistent");

        // Then
        assertThat(result).isEmpty();
    }

    // ===================== FIND BY ID WITH ROLES =====================

    @Test
    @DisplayName("Should find user with roles by ID")
    void findByIdWithRoles_ShouldReturnUserWithRoles_WhenIdExists() {
        // When
        Optional<User> result = userRepository.findByIdWithRoles(testUser1.getId());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(testUser1.getId());
        assertThat(result.get().getRoles()).hasSize(1);
        assertThat(result.get().getRoles())
                .extracting(UserRole::getRole)
                .containsExactly(RoleType.CUSTOMER);
    }

    @Test
    @DisplayName("Should return empty when ID does not exist with roles query")
    void findByIdWithRoles_ShouldReturnEmpty_WhenIdNotExists() {
        // When
        Optional<User> result = userRepository.findByIdWithRoles(999L);

        // Then
        assertThat(result).isEmpty();
    }

    // ===================== FIND BY ID IN WITH ROLES (BATCH) =====================

    @Test
    @DisplayName("Should find users by list of IDs with roles")
    void findByIdInWithRoles_ShouldReturnUsersWithRoles_WhenIdsExist() {
        // Given
        List<Long> ids = List.of(testUser1.getId(), testUser2.getId());

        // When
        List<User> result = userRepository.findByIdInWithRoles(ids);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(User::getId)
                .containsExactlyInAnyOrder(testUser1.getId(), testUser2.getId());

        // Verify roles are loaded
        User user1Result = result.stream()
                .filter(u -> u.getId().equals(testUser1.getId()))
                .findFirst()
                .orElseThrow();
        assertThat(user1Result.getRoles()).hasSize(1);

        User user2Result = result.stream()
                .filter(u -> u.getId().equals(testUser2.getId()))
                .findFirst()
                .orElseThrow();
        assertThat(user2Result.getRoles()).hasSize(2);
    }

    @Test
    @DisplayName("Should return empty list when no IDs match")
    void findByIdInWithRoles_ShouldReturnEmpty_WhenNoIdsMatch() {
        // Given
        List<Long> nonExistentIds = List.of(999L, 998L);

        // When
        List<User> result = userRepository.findByIdInWithRoles(nonExistentIds);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should return partial results when some IDs match")
    void findByIdInWithRoles_ShouldReturnPartialResults_WhenSomeIdsMatch() {
        // Given
        List<Long> mixedIds = List.of(testUser1.getId(), 999L);

        // When
        List<User> result = userRepository.findByIdInWithRoles(mixedIds);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(testUser1.getId());
    }

    // ===================== SAVE AND UPDATE =====================

    @Test
    @DisplayName("Should save new user successfully")
    void save_ShouldPersistUser_WhenNewUser() {
        // Given
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setEmail("newuser@example.com");
        newUser.setPasswordHash("$2a$10$encoded");
        newUser.setFullName("New User");
        newUser.setIsActive(true);

        UserRole role = UserRole.builder().role(RoleType.CUSTOMER).build();
        newUser.addRole(role);

        // When
        User savedUser = userRepository.save(newUser);
        entityManager.flush();

        // Then
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo("newuser");
        assertThat(savedUser.getCreatedAt()).isNotNull();
        assertThat(savedUser.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should update existing user successfully")
    void save_ShouldUpdateUser_WhenExistingUser() {
        // Given
        User existingUser = userRepository.findById(testUser1.getId()).orElseThrow();
        existingUser.setFullName("Updated Name");
        existingUser.setEmail("updated@example.com");

        // When
        User updatedUser = userRepository.save(existingUser);
        entityManager.flush();

        // Then
        assertThat(updatedUser.getId()).isEqualTo(testUser1.getId());
        assertThat(updatedUser.getFullName()).isEqualTo("Updated Name");
        assertThat(updatedUser.getEmail()).isEqualTo("updated@example.com");
    }

    // ===================== DELETE =====================

    @Test
    @DisplayName("Should delete user successfully")
    void delete_ShouldRemoveUser_WhenUserExists() {
        // Given
        Long userId = testUser1.getId();

        // When
        userRepository.delete(testUser1);
        entityManager.flush();

        // Then
        Optional<User> deletedUser = userRepository.findById(userId);
        assertThat(deletedUser).isEmpty();
    }

    // ===================== UNIQUE CONSTRAINTS =====================

    @Test
    @DisplayName("Should enforce unique username constraint")
    void save_ShouldThrowException_WhenUsernameNotUnique() {
        // Given
        User duplicateUser = new User();
        duplicateUser.setUsername("user1"); // Duplicate
        duplicateUser.setEmail("different@example.com");
        duplicateUser.setPasswordHash("$2a$10$encoded");
        duplicateUser.setFullName("Duplicate User");
        duplicateUser.setIsActive(true);

        UserRole role = UserRole.builder().role(RoleType.CUSTOMER).build();
        duplicateUser.addRole(role);

        // When / Then
        assertThatThrownBy(() -> {
            userRepository.save(duplicateUser);
            entityManager.flush();
        }).isInstanceOf(Exception.class); // DataIntegrityViolationException or similar
    }

    @Test
    @DisplayName("Should enforce unique email constraint")
    void save_ShouldThrowException_WhenEmailNotUnique() {
        // Given
        User duplicateUser = new User();
        duplicateUser.setUsername("differentuser");
        duplicateUser.setEmail("user1@example.com"); // Duplicate
        duplicateUser.setPasswordHash("$2a$10$encoded");
        duplicateUser.setFullName("Duplicate User");
        duplicateUser.setIsActive(true);

        UserRole role = UserRole.builder().role(RoleType.CUSTOMER).build();
        duplicateUser.addRole(role);

        // When / Then
        assertThatThrownBy(() -> {
            userRepository.save(duplicateUser);
            entityManager.flush();
        }).isInstanceOf(Exception.class); // DataIntegrityViolationException or similar
    }
}
