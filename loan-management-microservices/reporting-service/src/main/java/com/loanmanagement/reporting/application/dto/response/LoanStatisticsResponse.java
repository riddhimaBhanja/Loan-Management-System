package com.loanmanagement.reporting.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Response DTO for loan statistics
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanStatisticsResponse {
    private Long totalLoans;
    private Map<String, Long> loansByStatus; // PENDING: 10, APPROVED: 20, etc.
    private Map<String, Long> loansByType; // HOME_LOAN: 15, CAR_LOAN: 10, etc.
    private BigDecimal averageLoanAmount;
    private BigDecimal maxLoanAmount;
    private BigDecimal minLoanAmount;
    private BigDecimal totalDisbursed;
    private BigDecimal totalRequested;
}
