package com.loanmanagement.loanapp.application.controller;

import com.loanmanagement.common.dto.ApiResponse;
import com.loanmanagement.loanapp.application.dto.request.AssignOfficerRequest;
import com.loanmanagement.loanapp.application.dto.response.LoanResponse;
import com.loanmanagement.loanapp.domain.service.LoanApplicationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for loan officer assignment operations
 */
@RestController
@RequestMapping("/api/loans")
public class LoanAssignmentController {

    private static final Logger logger = LoggerFactory.getLogger(LoanAssignmentController.class);

    @Autowired
    private LoanApplicationService loanApplicationService;

    /**
     * Assign or reassign loan officer to a loan
     *
     * @param loanId Loan ID
     * @param request Assignment request
     * @param authentication Current user
     * @return Updated loan
     */
    @PutMapping("/{loanId}/assign-officer")
    @PreAuthorize("hasAnyRole('ADMIN', 'LOAN_OFFICER')")
    public ResponseEntity<ApiResponse> assignOfficer(
            @PathVariable Long loanId,
            @Valid @RequestBody AssignOfficerRequest request,
            Authentication authentication) {

        logger.info("Assigning officer {} to loan {}", request.getLoanOfficerId(), loanId);

        LoanResponse response = loanApplicationService.assignOfficer(loanId, request);

        return ResponseEntity.ok(ApiResponse.success(
                "Loan officer assigned successfully",
                response
        ));
    }

    /**
     * Unassign loan officer from a loan
     *
     * @param loanId Loan ID
     * @param authentication Current user
     * @return Updated loan
     */
    @PutMapping("/{loanId}/unassign-officer")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> unassignOfficer(
            @PathVariable Long loanId,
            Authentication authentication) {

        logger.info("Unassigning officer from loan {}", loanId);

        LoanResponse response = loanApplicationService.unassignOfficer(loanId);

        return ResponseEntity.ok(ApiResponse.success(
                "Loan officer unassigned successfully",
                response
        ));
    }

    /**
     * Get loans assigned to a specific officer
     *
     * @param officerId Officer ID
     * @return List of assigned loans
     */
    @GetMapping("/officer/{officerId}/assigned")
    @PreAuthorize("hasAnyRole('ADMIN', 'LOAN_OFFICER')")
    public ResponseEntity<ApiResponse> getAssignedLoans(
            @PathVariable Long officerId) {

        logger.info("Fetching loans assigned to officer {}", officerId);

        java.util.List<LoanResponse> loans = loanApplicationService.getLoansByOfficerId(officerId);

        return ResponseEntity.ok(ApiResponse.success(
                "Assigned loans retrieved successfully",
                loans
        ));
    }
}
