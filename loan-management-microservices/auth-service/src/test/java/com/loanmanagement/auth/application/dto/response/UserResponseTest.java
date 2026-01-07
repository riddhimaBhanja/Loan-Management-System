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
    @Test
    void equalsAndHashCode_shouldWorkCorrectly() {
        LocalDateTime now = LocalDateTime.now();
        Set<RoleType> roles = Set.of(RoleType.CUSTOMER);

        UserResponse r1 = UserResponse.builder()
                .id(1L)
                .username("user")
                .email("user@test.com")
                .fullName("User One")
                .phoneNumber("9876543210")
                .roles(roles)
                .isActive(true)
                .createdAt(now)
                .updatedAt(now)
                .build();

        UserResponse r2 = UserResponse.builder()
                .id(1L)
                .username("user")
                .email("user@test.com")
                .fullName("User One")
                .phoneNumber("9876543210")
                .roles(roles)
                .isActive(true)
                .createdAt(now)
                .updatedAt(now)
                .build();

        UserResponse r3 = UserResponse.builder()
                .id(2L)
                .username("other")
                .email("other@test.com")
                .fullName("Other User")
                .phoneNumber("9999999999")
                .roles(Set.of(RoleType.ADMIN))
                .isActive(false)
                .build();

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
        assertNotEquals(r1, r3);
    }

    @Test
    void equals_shouldReturnFalseForNullAndDifferentType() {
        UserResponse response = UserResponse.builder()
                .id(1L)
                .username("user")
                .email("user@test.com")
                .build();

        assertNotEquals(response, null);
        assertNotEquals(response, "string");
    }

    @Test
    void toString_shouldContainClassNameAndKeyFields() {
        UserResponse response = UserResponse.builder()
                .id(1L)
                .username("user")
                .email("user@test.com")
                .fullName("User One")
                .build();

        String value = response.toString();

        assertNotNull(value);
        assertTrue(value.contains("UserResponse"));
        assertTrue(value.contains("user"));
        assertTrue(value.contains("user@test.com"));
    }

}
