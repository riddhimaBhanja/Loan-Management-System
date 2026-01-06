package com.loanmanagement.common.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * User Details DTO for inter-service communication
 * Used by services to get user information from Auth Service
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDetailsDTO {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private Boolean isActive;
    private Set<String> roles;
    private LocalDateTime createdAt;
}
