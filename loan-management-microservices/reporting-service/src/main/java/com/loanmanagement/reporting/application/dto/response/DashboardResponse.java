package com.loanmanagement.reporting.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Response DTO for dashboard data
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DashboardResponse {

    // Overall Statistics
    private Long totalLoans;
    private Long totalCustomers;
    private Long pendingApprovals;
    private Long approvedLoans;
    private Long disbursedLoans;
    private BigDecimal totalDisbursedAmount;

    // EMI Statistics
    private BigDecimal totalEmiCollected;
    private BigDecimal pendingEmiAmount;
    private BigDecimal overdueAmount;
    private Integer overdueCount;

    // Recent Data
    private List<LoanSummary> recentLoans;

    // User Statistics (Admin only)
    private Long totalUsers;
    private Long activeUsers;

    // Customer-specific fields
    private Long myTotalLoans;
    private Long myActiveLoans;
    private Long myClosedLoans;
    private Long myPendingLoans;
    private BigDecimal totalOutstanding;
    private LocalDate nextEmiDueDate;
    private BigDecimal nextEmiAmount;
    private Long nextEmiLoanId;
    private List<LoanSummary> myLoans;
}
