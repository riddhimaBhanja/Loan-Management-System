package com.loanmanagement.auth.infrastructure.security.jwt;

import com.loanmanagement.auth.infrastructure.security.UserDetailsServiceImpl;
import com.loanmanagement.auth.domain.model.RoleType;
import com.loanmanagement.auth.domain.model.User;
import com.loanmanagement.auth.domain.model.UserRole;
import com.loanmanagement.auth.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void loadUserByUsername_success() {
        UserRole userRole = UserRole.builder()
                .role(RoleType.CUSTOMER)
                .build();

        User user = User.builder()
                .username("testuser")
                .passwordHash("password")
                .isActive(true)
                .roles(Collections.singleton(userRole))
                .build();

        when(userRepository.findByUsernameWithRoles("testuser"))
                .thenReturn(Optional.of(user));

        UserDetails userDetails =
                userDetailsService.loadUserByUsername("testuser");

        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
        assertTrue(userDetails.isEnabled());
        assertTrue(
                userDetails.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_CUSTOMER"))
        );
    }

    @Test
    void loadUserByUsername_userNotFound() {
        when(userRepository.findByUsernameWithRoles("unknown"))
                .thenReturn(Optional.empty());

        assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("unknown")
        );
    }

    @Test
    void loadUserByUsername_inactiveUser() {
        User user = User.builder()
                .username("inactive")
                .isActive(false)
                .build();

        when(userRepository.findByUsernameWithRoles("inactive"))
                .thenReturn(Optional.of(user));

        assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("inactive")
        );
    }
}
