package com.loanmanagement.reporting.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Response DTO for user statistics
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserStatisticsResponse {
    private Long totalUsers;
    private Long activeUsers;
    private Long inactiveUsers;
    private Map<String, Long> usersByRole; // CUSTOMER: 100, LOAN_OFFICER: 5, ADMIN: 2
}
