package com.loanmanagement.loanapproval.application.controller;

import com.loanmanagement.common.dto.ApiResponse;
import com.loanmanagement.common.dto.LoanDTO;
import com.loanmanagement.loanapproval.application.dto.request.ApproveLoanRequest;
import com.loanmanagement.loanapproval.application.dto.request.DisburseLoanRequest;
import com.loanmanagement.loanapproval.application.dto.request.RejectLoanRequest;
import com.loanmanagement.loanapproval.application.dto.response.LoanApprovalResponse;
import com.loanmanagement.loanapproval.application.dto.response.LoanDisbursementResponse;
import com.loanmanagement.loanapproval.domain.service.LoanApprovalService;
import com.loanmanagement.loanapproval.domain.service.LoanClosureService;
import com.loanmanagement.loanapproval.domain.service.LoanDisbursementService;
import com.loanmanagement.loanapproval.infrastructure.security.UserPrincipal;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for loan approval, disbursement and closure operations
 * Accessible by LOAN_OFFICER and ADMIN roles only
 */
@RestController
@RequestMapping("/api/loan-approvals")
public class LoanApprovalController {

    private static final Logger logger = LoggerFactory.getLogger(LoanApprovalController.class);

    private final LoanApprovalService loanApprovalService;
    private final LoanDisbursementService loanDisbursementService;
    private final LoanClosureService loanClosureService;

    public LoanApprovalController(
            LoanApprovalService loanApprovalService,
            LoanDisbursementService loanDisbursementService,
            LoanClosureService loanClosureService) {
        this.loanApprovalService = loanApprovalService;
        this.loanDisbursementService = loanDisbursementService;
        this.loanClosureService = loanClosureService;
    }

    /**
     * Approve a loan application
     * POST /api/loan-approvals/{loanId}/approve
     */
    @PostMapping("/{loanId}/approve")
    @PreAuthorize("hasAnyRole('LOAN_OFFICER', 'ADMIN')")
    public ResponseEntity<ApiResponse> approveLoan(
            @PathVariable Long loanId,
            @Valid @RequestBody ApproveLoanRequest request,
            Authentication authentication) {

        logger.info("Approve loan request received for loan ID: {} by user: {}",
                loanId, authentication.getName());

        Long approverId = getUserIdFromAuth(authentication);
        LoanApprovalResponse response = loanApprovalService.approveLoan(loanId, request, approverId);

        return ResponseEntity.ok(ApiResponse.success("Loan approved successfully", response));
    }

    /**
     * Reject a loan application
     * POST /api/loan-approvals/{loanId}/reject
     */
    @PostMapping("/{loanId}/reject")
    @PreAuthorize("hasAnyRole('LOAN_OFFICER', 'ADMIN')")
    public ResponseEntity<ApiResponse> rejectLoan(
            @PathVariable Long loanId,
            @Valid @RequestBody RejectLoanRequest request,
            Authentication authentication) {

        logger.info("Reject loan request received for loan ID: {} by user: {}",
                loanId, authentication.getName());

        Long approverId = getUserIdFromAuth(authentication);
        LoanApprovalResponse response = loanApprovalService.rejectLoan(loanId, request, approverId);

        return ResponseEntity.ok(ApiResponse.success("Loan rejected successfully", response));
    }

    /**
     * Disburse an approved loan
     * POST /api/loan-approvals/{loanId}/disburse
     */
    @PostMapping("/{loanId}/disburse")
    @PreAuthorize("hasAnyRole('LOAN_OFFICER', 'ADMIN')")
    public ResponseEntity<ApiResponse> disburseLoan(
            @PathVariable Long loanId,
            @Valid @RequestBody DisburseLoanRequest request,
            Authentication authentication) {

        logger.info("Disburse loan request received for loan ID: {} by user: {}",
                loanId, authentication.getName());

        Long disbursedBy = getUserIdFromAuth(authentication);
        LoanDisbursementResponse response = loanDisbursementService.disburseLoan(loanId, request, disbursedBy);

        return ResponseEntity.ok(ApiResponse.success("Loan disbursed successfully", response));
    }

    /**
     * Close a fully paid loan
     * POST /api/loan-approvals/{loanId}/close
     */
    @PostMapping("/{loanId}/close")
    @PreAuthorize("hasAnyRole('LOAN_OFFICER', 'ADMIN')")
    public ResponseEntity<ApiResponse> closeLoan(
            @PathVariable Long loanId,
            Authentication authentication) {

        logger.info("Close loan request received for loan ID: {} by user: {}",
                loanId, authentication.getName());

        LoanDTO response = loanClosureService.closeLoan(loanId);

        return ResponseEntity.ok(ApiResponse.success("Loan closed successfully", response));
    }

    /**
     * Get approval details by loan ID
     * GET /api/loan-approvals/loan/{loanId}
     */
    @GetMapping("/loan/{loanId}")
    @PreAuthorize("hasAnyRole('LOAN_OFFICER', 'ADMIN', 'CUSTOMER')")
    public ResponseEntity<ApiResponse> getApprovalByLoanId(@PathVariable Long loanId) {
        logger.info("Get approval details request for loan ID: {}", loanId);

        LoanApprovalResponse response = loanApprovalService.getApprovalByLoanId(loanId);
        return ResponseEntity.ok(ApiResponse.success("Approval details retrieved successfully", response));
    }

    /**
     * Get disbursement details by loan ID
     * GET /api/loan-approvals/disbursement/loan/{loanId}
     */
    @GetMapping("/disbursement/loan/{loanId}")
    @PreAuthorize("hasAnyRole('LOAN_OFFICER', 'ADMIN', 'CUSTOMER')")
    public ResponseEntity<ApiResponse> getDisbursementByLoanId(@PathVariable Long loanId) {
        logger.info("Get disbursement details request for loan ID: {}", loanId);

        LoanDisbursementResponse response = loanDisbursementService.getDisbursementByLoanId(loanId);
        return ResponseEntity.ok(ApiResponse.success("Disbursement details retrieved successfully", response));
    }

    /**
     * Extract user ID from authentication
     */
    private Long getUserIdFromAuth(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return userPrincipal.getUserId();
    }
}
