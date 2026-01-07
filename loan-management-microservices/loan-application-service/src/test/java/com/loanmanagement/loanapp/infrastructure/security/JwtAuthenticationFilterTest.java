package com.loanmanagement.loanapp.infrastructure.security;

import com.loanmanagement.common.security.SecurityConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() throws Exception {
        SecurityContextHolder.clearContext();
        closeable.close();
    }

    @Test
    void shouldAuthenticateUsingHeadersFromApiGateway() throws ServletException, IOException {
        when(request.getHeader("X-User-Id")).thenReturn("10");
        when(request.getHeader("X-Username")).thenReturn("john");
        when(request.getHeader("X-User-Roles")).thenReturn("ROLE_USER");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        assertNotNull(auth);
        assertTrue(auth.getPrincipal() instanceof UserPrincipal);
        assertEquals("john", ((UserPrincipal) auth.getPrincipal()).getUsername());

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtUtil);
    }

    @Test
    void shouldAuthenticateUsingJwtWhenHeadersMissing() throws ServletException, IOException {
        when(request.getHeader("X-User-Id")).thenReturn(null);
        when(request.getHeader("X-Username")).thenReturn(null);
        when(request.getHeader(SecurityConstants.HEADER_STRING))
                .thenReturn(SecurityConstants.TOKEN_PREFIX + "valid.jwt.token");

        when(jwtUtil.validateToken("valid.jwt.token")).thenReturn(true);
        when(jwtUtil.getUsernameFromToken("valid.jwt.token")).thenReturn("jane");
        when(jwtUtil.getUserIdFromToken("valid.jwt.token")).thenReturn(20L);
        when(jwtUtil.getRolesFromToken("valid.jwt.token")).thenReturn("ROLE_ADMIN");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        assertNotNull(auth);
        assertTrue(auth.getPrincipal() instanceof UserPrincipal);
        assertEquals("jane", ((UserPrincipal) auth.getPrincipal()).getUsername());

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldNotAuthenticateWhenJwtIsInvalid() throws ServletException, IOException {
        when(request.getHeader(SecurityConstants.HEADER_STRING))
                .thenReturn(SecurityConstants.TOKEN_PREFIX + "invalid.jwt");

        when(jwtUtil.validateToken("invalid.jwt")).thenReturn(false);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldNotAuthenticateWhenNoHeadersAndNoJwt() throws ServletException, IOException {
        when(request.getHeader("X-User-Id")).thenReturn(null);
        when(request.getHeader("X-Username")).thenReturn(null);
        when(request.getHeader(SecurityConstants.HEADER_STRING)).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldHandleExceptionGracefullyAndContinueFilterChain() throws ServletException, IOException {
        when(request.getHeader("X-User-Id")).thenThrow(new RuntimeException("Header error"));

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldIgnoreBearerTokenWithoutPrefix() throws ServletException, IOException {
        when(request.getHeader(SecurityConstants.HEADER_STRING))
                .thenReturn("InvalidTokenWithoutBearer");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtUtil);
    }
}
