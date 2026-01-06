package com.loanmanagement.reporting.infrastructure.security;

import com.loanmanagement.common.security.SecurityConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT Authentication Filter for microservices
 * Validates JWT tokens without loading user from database
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        try {
            // First, try to get user info from headers (forwarded by API Gateway)
            String headerUserId = request.getHeader("X-User-Id");
            String headerUsername = request.getHeader("X-Username");
            String headerRoles = request.getHeader("X-User-Roles");

            if (StringUtils.hasText(headerUserId) && StringUtils.hasText(headerUsername)) {
                // Use headers from API Gateway (preferred method)
                Long userId = Long.parseLong(headerUserId);
                UserPrincipal userPrincipal = UserPrincipal.from(headerUsername, userId, headerRoles != null ? headerRoles : "");

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userPrincipal,
                                null,
                                userPrincipal.getAuthorities()
                        );

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);

                logger.debug("Set authentication from headers for user: {} (ID: {}) with roles: {}",
                        headerUsername, userId, headerRoles);
            } else {
                // Fallback to JWT token (for direct service calls, if any)
                String jwt = getJwtFromRequest(request);

                if (StringUtils.hasText(jwt) && jwtUtil.validateToken(jwt)) {
                    String username = jwtUtil.getUsernameFromToken(jwt);
                    Long userId = jwtUtil.getUserIdFromToken(jwt);
                    String roles = jwtUtil.getRolesFromToken(jwt);

                    if (username != null && userId != null) {
                        UserPrincipal userPrincipal = UserPrincipal.from(username, userId, roles != null ? roles : "");

                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        userPrincipal,
                                        null,
                                        userPrincipal.getAuthorities()
                                );

                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);

                        logger.debug("Set authentication from JWT for user: {} (ID: {}) with roles: {}",
                                username, userId, roles);
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extract JWT token from Authorization header
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(SecurityConstants.HEADER_STRING);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            return bearerToken.substring(7);
        }

        return null;
    }
}
