package com.loanmanagement.loanapproval.infrastructure.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class UserPrincipalTest {

    @Test
    void from_createsUserPrincipalWithRoles() {
        String username = "testuser";
        Long userId = 1L;
        String roles = "ADMIN,USER";

        UserPrincipal userPrincipal = UserPrincipal.from(username, userId, roles);

        assertNotNull(userPrincipal);
        assertEquals(username, userPrincipal.getUsername());
        assertEquals(userId, userPrincipal.getUserId());

        Collection<? extends GrantedAuthority> authorities =
                userPrincipal.getAuthorities();

        assertNotNull(authorities);
        assertEquals(2, authorities.size());
        assertTrue(
                authorities.stream()
                        .anyMatch(a -> a.getAuthority().equals("ADMIN"))
        );
        assertTrue(
                authorities.stream()
                        .anyMatch(a -> a.getAuthority().equals("USER"))
        );
    }

    @Test
    void from_handlesEmptyRolesGracefully() {
        UserPrincipal userPrincipal =
                UserPrincipal.from("user", 2L, "");

        assertNotNull(userPrincipal);
        assertTrue(userPrincipal.getAuthorities().isEmpty());
    }

    @Test
    void from_handlesNullAndBlankRoles() {
        UserPrincipal userPrincipal =
                UserPrincipal.from("user", 3L, " , , ");

        assertNotNull(userPrincipal);
        assertTrue(userPrincipal.getAuthorities().isEmpty());
    }

    @Test
    void userDetailsDefaults_areAllTrue() {
        UserPrincipal userPrincipal =
                UserPrincipal.from("user", 4L, "USER");

        assertTrue(userPrincipal.isAccountNonExpired());
        assertTrue(userPrincipal.isAccountNonLocked());
        assertTrue(userPrincipal.isCredentialsNonExpired());
        assertTrue(userPrincipal.isEnabled());
        assertNull(userPrincipal.getPassword());
    }
}
