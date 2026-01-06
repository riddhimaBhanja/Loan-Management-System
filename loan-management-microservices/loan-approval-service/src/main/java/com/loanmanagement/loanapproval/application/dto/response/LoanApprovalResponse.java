package com.loanmanagement.loanapproval.application.dto.response;

import com.loanmanagement.loanapproval.domain.model.LoanApproval;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response DTO for loan approval
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanApprovalResponse {

    private Long id;
    private Long loanId;
    private Long approverId;
    private String approverName;
    private LoanApproval.ApprovalStatus status;
    private BigDecimal approvedAmount;
    private BigDecimal interestRate;
    private LocalDateTime decisionDate;
    private String rejectionReason;
    private String notes;
    private LocalDateTime createdAt;
}
