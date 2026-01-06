package com.loanmanagement.reporting.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Response DTO for customer summary
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerSummaryResponse {
    private Long customerId;
    private String customerName;
    private String customerEmail;
    private Long totalLoans;
    private BigDecimal totalBorrowed;
    private BigDecimal totalPaid;
    private BigDecimal totalPending;
    private Integer activeLoans;
    private Integer closedLoans;
    private Integer rejectedLoans;
    private List<LoanSummary> loans;
}
