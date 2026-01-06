package com.loanmanagement.common.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Loan DTO for inter-service communication
 * Shared between Loan Application Service and Loan Approval Service
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanDTO {
    private Long id;
    private String applicationNumber;
    private Long customerId;
    private Long loanTypeId;
    private Long loanOfficerId;
    private BigDecimal requestedAmount;
    private BigDecimal approvedAmount;
    private Integer tenureMonths;
    private BigDecimal interestRate;
    private BigDecimal monthlyEmi;
    private BigDecimal totalInterest;
    private BigDecimal totalPayable;
    private String status;
    private String employmentStatus;
    private BigDecimal monthlyIncome;
    private String purpose;
    private String remarks;
    private LocalDateTime appliedAt;
    private LocalDateTime reviewedAt;
    private LocalDateTime approvedAt;
    private LocalDateTime disbursedAt;
    private String disbursementMethod;
    private String disbursementReference;
    private LocalDateTime closedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
