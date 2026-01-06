package com.loanmanagement.auth.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for UserRole entity
 * Focused on lifecycle, equals/hashCode, and Lombok coverage
 */
class UserRoleTest {

    /* ===================== HELPERS ===================== */

    private User createUser(Long id) {
        User user = new User();
        user.setId(id);
        user.setUsername("user" + id);
        user.setEmail("user" + id + "@test.com");
        user.setPasswordHash("pwd");
        user.setFullName("Test User");
        user.setPhoneNumber("9999999999");
        return user;
    }

    /* ===================== LIFECYCLE ===================== */

    @Test
    void onCreate_shouldInitializeCreatedAt() {
        UserRole role = new UserRole();
        role.setUser(createUser(1L));
        role.setRole(RoleType.ADMIN);

        role.onCreate();

        assertNotNull(role.getCreatedAt());
    }

    @Test
    void onCreate_shouldNotOverrideExistingCreatedAt() {
        LocalDateTime time = LocalDateTime.now().minusDays(1);

        UserRole role = new UserRole();
        role.setUser(createUser(1L));
        role.setRole(RoleType.ADMIN);
        role.setCreatedAt(time);

        role.onCreate();

        assertEquals(time, role.getCreatedAt());
    }

    /* ===================== EQUALS ===================== */

    @Test
    void equals_shouldReturnTrue_forSameUserIdAndRole() {
        User user1 = createUser(1L);
        User user2 = createUser(1L);

        UserRole r1 = new UserRole();
        r1.setUser(user1);
        r1.setRole(RoleType.ADMIN);

        UserRole r2 = new UserRole();
        r2.setUser(user2);
        r2.setRole(RoleType.ADMIN);

        assertEquals(r1, r2);
    }

    @Test
    void equals_shouldReturnFalse_forDifferentRole() {
        User user = createUser(1L);

        UserRole r1 = new UserRole();
        r1.setUser(user);
        r1.setRole(RoleType.ADMIN);

        UserRole r2 = new UserRole();
        r2.setUser(user);
        r2.setRole(RoleType.CUSTOMER);

        assertNotEquals(r1, r2);
    }

    @Test
    void equals_shouldReturnFalse_forDifferentUserId() {
        UserRole r1 = new UserRole();
        r1.setUser(createUser(1L));
        r1.setRole(RoleType.ADMIN);

        UserRole r2 = new UserRole();
        r2.setUser(createUser(2L));
        r2.setRole(RoleType.ADMIN);

        assertNotEquals(r1, r2);
    }

    @Test
    void equals_shouldReturnFalse_forDifferentObject() {
        UserRole role = new UserRole();
        role.setUser(createUser(1L));
        role.setRole(RoleType.ADMIN);

        assertNotEquals(role, "ROLE_ADMIN");
    }

    /* ===================== HASHCODE ===================== */

    @Test
    void hashCode_shouldBeConsistent() {
        UserRole role = new UserRole();
        role.setUser(createUser(1L));
        role.setRole(RoleType.ADMIN);

        assertEquals(role.hashCode(), role.hashCode());
    }

    /* ===================== LOMBOK COVERAGE ===================== */

    @Test
    void shouldCoverBuilderSettersAndToString() {
        UserRole role = UserRole.builder()
                .id(1L)
                .user(createUser(1L))
                .role(RoleType.ADMIN)
                .createdAt(LocalDateTime.now())
                .build();

        role.setId(2L);
        role.setRole(RoleType.CUSTOMER);

        String text = role.toString();

        assertNotNull(text);
        assertTrue(text.contains("UserRole"));
    }
}
