package com.loanmanagement.loanapproval.infrastructure.security;

import com.loanmanagement.common.security.SecurityConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_setsAuthentication_fromHeaders() throws ServletException, IOException {
        request.addHeader("X-User-Id", "1");
        request.addHeader("X-Username", "testuser");
        request.addHeader("X-User-Roles", "ADMIN,USER");

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("testuser",
                ((UserPrincipal) SecurityContextHolder.getContext()
                        .getAuthentication().getPrincipal()).getUsername());

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtUtil);
    }

    @Test
    void doFilterInternal_setsAuthentication_fromJwt_whenHeadersMissing() throws ServletException, IOException {
        request.addHeader(
                SecurityConstants.HEADER_STRING,
                SecurityConstants.TOKEN_PREFIX + "test.jwt.token"
        );

        when(jwtUtil.validateToken("test.jwt.token")).thenReturn(true);
        when(jwtUtil.getUsernameFromToken("test.jwt.token")).thenReturn("jwtuser");
        when(jwtUtil.getUserIdFromToken("test.jwt.token")).thenReturn(2L);
        when(jwtUtil.getRolesFromToken("test.jwt.token")).thenReturn("USER");

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("jwtuser",
                ((UserPrincipal) SecurityContextHolder.getContext()
                        .getAuthentication().getPrincipal()).getUsername());

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_noAuthentication_whenNoHeadersAndInvalidJwt() throws ServletException, IOException {
        request.addHeader(
                SecurityConstants.HEADER_STRING,
                SecurityConstants.TOKEN_PREFIX + "invalid.jwt"
        );

        when(jwtUtil.validateToken("invalid.jwt")).thenReturn(false);

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_handlesException_gracefully() throws ServletException, IOException {
        request.addHeader("X-User-Id", "abc"); // invalid number
        request.addHeader("X-Username", "user");

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }
}
