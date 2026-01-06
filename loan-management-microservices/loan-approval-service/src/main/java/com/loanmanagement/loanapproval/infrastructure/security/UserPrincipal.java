package com.loanmanagement.loanapproval.infrastructure.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Custom UserDetails implementation that includes userId
 * Used to pass user context from API Gateway to services
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPrincipal implements UserDetails {

    private static final long serialVersionUID = 1L;

    private Long userId;
    private String username;
    private Collection<? extends GrantedAuthority> authorities;

    /**
     * Create UserPrincipal from headers forwarded by API Gateway
     */
    public static UserPrincipal from(String username, Long userId, String roles) {
        Collection<GrantedAuthority> authorities = Stream.of(roles.split(","))
                .filter(role -> role != null && !role.trim().isEmpty())
                .map(role -> new SimpleGrantedAuthority(role.trim()))
                .collect(Collectors.toList());

        return UserPrincipal.builder()
                .userId(userId)
                .username(username)
                .authorities(authorities)
                .build();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return null; // Not used in this context
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
