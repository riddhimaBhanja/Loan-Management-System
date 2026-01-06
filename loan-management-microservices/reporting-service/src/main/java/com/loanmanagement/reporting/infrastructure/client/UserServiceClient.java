package com.loanmanagement.reporting.infrastructure.client;

import com.loanmanagement.common.dto.UserDetailsDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

/**
 * Feign Client for Auth Service (User Management)
 */
@FeignClient(name = "AUTH-SERVICE")
public interface UserServiceClient {

    @GetMapping("/api/internal/users/count")
    Long getTotalUsers();

    @GetMapping("/api/internal/users/active/count")
    Long getActiveUsersCount();

    @GetMapping("/api/internal/users/role/{role}/count")
    Long getUsersByRole(@PathVariable String role);

    @GetMapping("/api/internal/users/statistics")
    Map<String, Object> getUserStatistics();

    @GetMapping("/api/internal/users/{userId}")
    UserDetailsDTO getUserById(@PathVariable Long userId);

    @GetMapping("/api/internal/users")
    List<UserDetailsDTO> getAllUsers();
}
