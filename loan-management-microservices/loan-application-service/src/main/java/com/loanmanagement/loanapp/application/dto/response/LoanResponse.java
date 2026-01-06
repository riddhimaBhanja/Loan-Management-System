package com.loanmanagement.loanapp.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.loanmanagement.loanapp.domain.enums.EmploymentStatus;
import com.loanmanagement.loanapp.domain.enums.LoanStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response DTO for Loan entity
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoanResponse {

    private Long id;
    private Long customerId;
    private String customerName;  // Added for frontend display
    private Long loanTypeId;
    private String loanTypeName;  // Added for frontend display
    private Long loanOfficerId;
    private String applicationNumber;  // Added for frontend display
    private BigDecimal amount;
    private BigDecimal requestedAmount;  // Alias for amount
    private Integer tenureMonths;
    private EmploymentStatus employmentStatus;
    private BigDecimal monthlyIncome;
    private String purpose;
    private LoanStatus status;
    private LocalDateTime appliedDate;
    private LocalDateTime appliedAt;  // Alias for appliedDate
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
