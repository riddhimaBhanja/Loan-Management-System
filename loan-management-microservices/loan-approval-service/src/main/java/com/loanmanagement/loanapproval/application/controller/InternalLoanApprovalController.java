package com.loanmanagement.loanapproval.application.controller;

import com.loanmanagement.loanapproval.application.dto.response.LoanApprovalResponse;
import com.loanmanagement.loanapproval.application.dto.response.LoanDisbursementResponse;
import com.loanmanagement.loanapproval.domain.service.LoanApprovalService;
import com.loanmanagement.loanapproval.domain.service.LoanDisbursementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Internal REST Controller for inter-service communication
 * No authentication required - should only be accessible within service mesh
 */
@RestController
@RequestMapping("/api/internal/loan-approvals")
public class InternalLoanApprovalController {

    private static final Logger logger = LoggerFactory.getLogger(InternalLoanApprovalController.class);

    private final LoanApprovalService loanApprovalService;
    private final LoanDisbursementService loanDisbursementService;

    public InternalLoanApprovalController(
            LoanApprovalService loanApprovalService,
            LoanDisbursementService loanDisbursementService) {
        this.loanApprovalService = loanApprovalService;
        this.loanDisbursementService = loanDisbursementService;
    }

    /**
     * Get approval details by loan ID (internal)
     * GET /api/internal/loan-approvals/loan/{loanId}
     */
    @GetMapping("/loan/{loanId}")
    public ResponseEntity<LoanApprovalResponse> getApprovalByLoanId(@PathVariable Long loanId) {
        logger.info("Internal request: Get approval details for loan ID: {}", loanId);

        LoanApprovalResponse response = loanApprovalService.getApprovalByLoanId(loanId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get disbursement details by loan ID (internal)
     * GET /api/internal/loan-approvals/disbursement/loan/{loanId}
     */
    @GetMapping("/disbursement/loan/{loanId}")
    public ResponseEntity<LoanDisbursementResponse> getDisbursementByLoanId(@PathVariable Long loanId) {
        logger.info("Internal request: Get disbursement details for loan ID: {}", loanId);

        LoanDisbursementResponse response = loanDisbursementService.getDisbursementByLoanId(loanId);
        return ResponseEntity.ok(response);
    }

    /**
     * Check if loan is approved (internal)
     * GET /api/internal/loan-approvals/loan/{loanId}/is-approved
     */
    @GetMapping("/loan/{loanId}/is-approved")
    public ResponseEntity<Boolean> isLoanApproved(@PathVariable Long loanId) {
        logger.info("Internal request: Check if loan {} is approved", loanId);

        try {
            LoanApprovalResponse approval = loanApprovalService.getApprovalByLoanId(loanId);
            boolean isApproved = approval.getStatus().toString().equals("APPROVED");
            return ResponseEntity.ok(isApproved);
        } catch (Exception e) {
            return ResponseEntity.ok(false);
        }
    }

    /**
     * Check if loan is disbursed (internal)
     * GET /api/internal/loan-approvals/loan/{loanId}/is-disbursed
     */
    @GetMapping("/loan/{loanId}/is-disbursed")
    public ResponseEntity<Boolean> isLoanDisbursed(@PathVariable Long loanId) {
        logger.info("Internal request: Check if loan {} is disbursed", loanId);

        try {
            loanDisbursementService.getDisbursementByLoanId(loanId);
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            return ResponseEntity.ok(false);
        }
    }

    /**
     * Get pending approvals count (for reporting service)
     * GET /api/internal/loan-approvals/pending/count
     */
    @GetMapping("/pending/count")
    public ResponseEntity<Long> getPendingApprovalsCount() {
        logger.info("Internal request: Get pending approvals count");
        Long count = loanApprovalService.getPendingApprovalsCount();
        return ResponseEntity.ok(count);
    }

    /**
     * Get approved loans count (for reporting service)
     * GET /api/internal/loan-approvals/approved/count
     */
    @GetMapping("/approved/count")
    public ResponseEntity<Long> getApprovedLoansCount() {
        logger.info("Internal request: Get approved loans count");
        Long count = loanApprovalService.getApprovedLoansCount();
        return ResponseEntity.ok(count);
    }

    /**
     * Get pending approvals count by officer (for reporting service)
     * GET /api/internal/loan-approvals/pending/officer/{officerId}/count
     */
    @GetMapping("/pending/officer/{officerId}/count")
    public ResponseEntity<Long> getPendingApprovalsByOfficerIdCount(@PathVariable Long officerId) {
        logger.info("Internal request: Get pending approvals count for officer {}", officerId);
        Long count = loanApprovalService.getPendingApprovalsByOfficerId(officerId);
        return ResponseEntity.ok(count);
    }
}
