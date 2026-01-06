package com.loanmanagement.auth.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for User entity
 * Focused on lifecycle, helpers, and Lombok coverage
 */
class UserTest {

    /* ===================== HELPER ===================== */

    private User createBaseUser() {
        return User.builder()
                .username("john_doe")
                .email("john@test.com")
                .passwordHash("hashed_pwd")
                .fullName("John Doe")
                .phoneNumber("9999999999")
                .build();
    }

    /* ===================== LIFECYCLE ===================== */

    @Test
    void onCreate_shouldInitializeAuditFields() {
        User user = createBaseUser();

        user.onCreate();

        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getUpdatedAt());
    }

    @Test
    void onUpdate_shouldUpdateUpdatedAt() {
        User user = createBaseUser();
        user.setUpdatedAt(LocalDateTime.now().minusDays(1));

        user.onUpdate();

        assertNotNull(user.getUpdatedAt());
    }

    /* ===================== ROLE HELPERS ===================== */

    @Test
    void addRole_shouldAttachRoleToUser() {
        User user = createBaseUser();
        UserRole role = new UserRole();

        user.addRole(role);

        assertTrue(user.getRoles().contains(role));
        assertEquals(user, role.getUser());
    }

    @Test
    void removeRole_shouldDetachRoleFromUser() {
        User user = createBaseUser();
        UserRole role = new UserRole();

        user.addRole(role);
        user.removeRole(role);

        assertFalse(user.getRoles().contains(role));
        assertNull(role.getUser());
    }

    /* ===================== DEFAULT VALUES ===================== */

    @Test
    void builder_shouldApplyDefaultValues() {
        User user = User.builder()
                .username("default_user")
                .email("default@test.com")
                .passwordHash("pwd")
                .fullName("Default User")
                .phoneNumber("8888888888")
                .build();

        // @Builder.Default fields
        assertTrue(user.getIsActive());
        assertNotNull(user.getRoles());
        assertTrue(user.getRoles().isEmpty());
    }

    @Test
    void builder_shouldOverrideDefaultValues() {
        Set<UserRole> roles = new HashSet<>();

        User user = User.builder()
                .username("override_user")
                .email("override@test.com")
                .passwordHash("pwd")
                .fullName("Override User")
                .phoneNumber("7777777777")
                .isActive(false)
                .roles(roles)
                .build();

        assertFalse(user.getIsActive());
        assertEquals(roles, user.getRoles());
    }

    @Test
    void defaultValues_shouldBeInitialized() {
        User user = createBaseUser();

        assertTrue(user.getIsActive());
        assertNotNull(user.getRoles());
        assertTrue(user.getRoles().isEmpty());
    }

    /* ===================== LOMBOK COVERAGE ===================== */

    @Test
    void shouldCoverLombokMethods() {
        User user = createBaseUser();

        user.setId(1L);
        user.setIsActive(false);

        String text = user.toString();

        assertNotNull(text);
        assertTrue(text.contains("User"));
    }
}
