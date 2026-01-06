package com.loanmanagement.loanapp.application.controller;

import com.loanmanagement.common.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Health check controller for testing authentication
 */
@RestController
@RequestMapping("/api/health")
@Slf4j
public class HealthController {

    @GetMapping("/public")
    public ResponseEntity<ApiResponse> publicHealth() {
        Map<String, Object> data = new HashMap<>();
        data.put("service", "loan-application-service");
        data.put("status", "UP");
        data.put("timestamp", LocalDateTime.now());
        return ResponseEntity.ok(ApiResponse.success("Service is running", data));
    }

    @GetMapping("/authenticated")
    public ResponseEntity<ApiResponse> authenticatedHealth() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Map<String, Object> data = new HashMap<>();
        data.put("service", "loan-application-service");
        data.put("status", "UP");
        data.put("authenticated", auth != null && auth.isAuthenticated());
        data.put("username", auth != null ? auth.getName() : "anonymous");
        data.put("authorities", auth != null ? auth.getAuthorities() : null);
        data.put("timestamp", LocalDateTime.now());

        log.info("Authenticated health check - User: {}, Authorities: {}",
                auth != null ? auth.getName() : "anonymous",
                auth != null ? auth.getAuthorities() : null);

        return ResponseEntity.ok(ApiResponse.success("Authentication verified", data));
    }
}
