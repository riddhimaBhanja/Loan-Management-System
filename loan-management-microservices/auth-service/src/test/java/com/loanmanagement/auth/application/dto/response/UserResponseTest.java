package com.loanmanagement.auth.application.dto.response;

import com.loanmanagement.auth.domain.model.RoleType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserResponseTest {

    @Test
    void shouldCreateUserResponseWithBuilder() {
        LocalDateTime now = LocalDateTime.now();

        UserResponse response = UserResponse.builder()
                .id(1L)
                .username("john_doe")
                .email("john@example.com")
                .fullName("John Doe")
                .phoneNumber("9876543210")
                .roles(Set.of(RoleType.CUSTOMER))
                .isActive(true)
                .createdAt(now)
                .build();

        assertEquals(1L, response.getId());
        assertEquals("john_doe", response.getUsername());
        assertEquals("john@example.com", response.getEmail());
        assertEquals("John Doe", response.getFullName());
        assertEquals("9876543210", response.getPhoneNumber());
        assertEquals(Set.of(RoleType.CUSTOMER), response.getRoles());
        assertTrue(response.getIsActive());
        assertEquals(now, response.getCreatedAt());
    }

    @Test
    void shouldSupportNoArgsConstructor() {
        UserResponse response = new UserResponse();

        assertNull(response.getId());
        assertNull(response.getUsername());
        assertNull(response.getEmail());
        assertNull(response.getFullName());
        assertNull(response.getPhoneNumber());
        assertNull(response.getRoles());
        assertNull(response.getIsActive());
        assertNull(response.getCreatedAt());
        assertNull(response.getUpdatedAt());
    }

    @Test
    void shouldSupportAllArgsConstructor() {
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        Set<RoleType> roles = Set.of(RoleType.ADMIN);

        UserResponse response = new UserResponse(
                2L,
                "admin_user",
                "admin@example.com",
                "Admin User",
                "9999999999",
                roles,
                true,
                createdAt,
                updatedAt
        );

        assertEquals(2L, response.getId());
        assertEquals("admin_user", response.getUsername());
        assertEquals("admin@example.com", response.getEmail());
        assertEquals("Admin User", response.getFullName());
        assertEquals("9999999999", response.getPhoneNumber());
        assertEquals(roles, response.getRoles());
        assertTrue(response.getIsActive());
        assertEquals(createdAt, response.getCreatedAt());
        assertEquals(updatedAt, response.getUpdatedAt());
    }
}
