package com.loanmanagement.loanapproval.application.controller;

import com.loanmanagement.common.dto.LoanDTO;
import com.loanmanagement.loanapproval.domain.service.LoanApprovalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for loan review operations
 * This controller handles /api/loans/** paths routed from API Gateway
 */
@RestController
@RequestMapping("/api/loans")
public class LoanReviewController {

    private static final Logger logger = LoggerFactory.getLogger(LoanReviewController.class);

    private final LoanApprovalService loanApprovalService;

    public LoanReviewController(LoanApprovalService loanApprovalService) {
        this.loanApprovalService = loanApprovalService;
    }

    /**
     * Review a loan application (get loan details for review before approval/rejection)
     * GET /api/loans/{loanId}/review
     */
    @GetMapping("/{loanId}/review")
    @PreAuthorize("hasAnyRole('LOAN_OFFICER', 'ADMIN')")
    public ResponseEntity<LoanDTO> reviewLoan(@PathVariable Long loanId) {
        logger.info("Review loan request for loan ID: {}", loanId);

        LoanDTO response = loanApprovalService.getLoanForReview(loanId);
        return ResponseEntity.ok(response);
    }
}
