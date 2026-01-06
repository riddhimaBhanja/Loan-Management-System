package com.loanmanagement.auth.infrastructure.security.jwt;

import com.loanmanagement.common.security.SecurityConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter filter;

    @BeforeEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldAuthenticateUserWhenValidJwtProvided() throws Exception {
        String token = "valid-token";
        String username = "john";

        when(request.getHeader(SecurityConstants.HEADER_STRING))
                .thenReturn(SecurityConstants.TOKEN_PREFIX + token);
        when(tokenProvider.validateToken(token)).thenReturn(true);
        when(tokenProvider.getUsernameFromToken(token)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username))
                .thenReturn(new User(username, "password", List.of()));

        filter.doFilter(request, response, filterChain);

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        assertNotNull(authentication);
        assertEquals(username, authentication.getName());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldNotAuthenticateWhenTokenIsInvalid() throws Exception {
        String token = "invalid-token";

        when(request.getHeader(SecurityConstants.HEADER_STRING))
                .thenReturn(SecurityConstants.TOKEN_PREFIX + token);
        when(tokenProvider.validateToken(token)).thenReturn(false);

        filter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldNotAuthenticateWhenAuthorizationHeaderMissing() throws Exception {
        when(request.getHeader(SecurityConstants.HEADER_STRING))
                .thenReturn(null);

        filter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldContinueFilterChainWhenExceptionOccurs() throws Exception {
        String token = "token";

        when(request.getHeader(SecurityConstants.HEADER_STRING))
                .thenReturn(SecurityConstants.TOKEN_PREFIX + token);
        when(tokenProvider.validateToken(token))
                .thenThrow(new RuntimeException("JWT error"));

        filter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }
}
