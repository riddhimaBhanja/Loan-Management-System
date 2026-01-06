package com.loanmanagement.loanapproval.domain.service;

import com.loanmanagement.common.dto.LoanDTO;
import com.loanmanagement.loanapproval.application.dto.request.ApproveLoanRequest;
import com.loanmanagement.loanapproval.application.dto.request.RejectLoanRequest;
import com.loanmanagement.loanapproval.application.dto.response.LoanApprovalResponse;

/**
 * Service interface for loan approval operations
 */
public interface LoanApprovalService {

    /**
     * Approve a loan application
     */
    LoanApprovalResponse approveLoan(Long loanId, ApproveLoanRequest request, Long approverId);

    /**
     * Reject a loan application
     */
    LoanApprovalResponse rejectLoan(Long loanId, RejectLoanRequest request, Long approverId);

    /**
     * Get loan details for review
     */
    LoanDTO getLoanForReview(Long loanId);

    /**
     * Get approval details by loan ID
     */
    LoanApprovalResponse getApprovalByLoanId(Long loanId);

    /**
     * Get count of pending approvals
     */
    Long getPendingApprovalsCount();

    /**
     * Get count of approved loans
     */
    Long getApprovedLoansCount();

    /**
     * Get count of pending approvals by officer ID
     */
    Long getPendingApprovalsByOfficerId(Long officerId);
}
