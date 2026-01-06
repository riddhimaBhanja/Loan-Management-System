package com.loanmanagement.emi.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Feign client for User Service (Auth Service)
 */
@FeignClient(name = "AUTH-SERVICE", path = "/api/internal/users")
public interface UserServiceClient {

    /**
     * Get user name by user ID
     *
     * @param userId User ID
     * @return User full name
     */
    @GetMapping("/{userId}/name")
    String getUserName(@PathVariable("userId") Long userId);

    /**
     * Get user ID by username
     *
     * @param username Username
     * @return User ID
     */
    @GetMapping("/by-username")
    Long getUserIdByUsername(@RequestParam("username") String username);

    /**
     * Verify user exists
     *
     * @param userId User ID
     * @return true if exists
     */
    @GetMapping("/{userId}/exists")
    boolean userExists(@PathVariable("userId") Long userId);
}
