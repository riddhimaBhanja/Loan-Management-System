package com.loanmanagement.loanapp.application.controller;

import com.loanmanagement.common.dto.LoanDTO;
import com.loanmanagement.loanapp.application.dto.response.LoanResponse;
import com.loanmanagement.loanapp.domain.service.LoanApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Internal API controller for inter-service communication
 * This endpoint is called by other microservices (not exposed via API Gateway)
 */
@RestController
@RequestMapping("/api/internal/loans")
@RequiredArgsConstructor
@Slf4j
public class InternalLoanController {

    private final LoanApplicationService loanApplicationService;

    /**
     * Get loan by ID (for other microservices)
     */
    @GetMapping("/{loanId}")
    public ResponseEntity<LoanDTO> getLoanById(@PathVariable Long loanId) {
        log.debug("Internal API: Fetching loan with ID: {}", loanId);
        LoanResponse response = loanApplicationService.getLoanById(loanId);

        // Convert LoanResponse to LoanDTO for inter-service communication
        LoanDTO loanDTO = LoanDTO.builder()
                .id(response.getId())
                .customerId(response.getCustomerId())
                .loanTypeId(response.getLoanTypeId())
                .loanOfficerId(response.getLoanOfficerId())
                .requestedAmount(response.getAmount())  // Map amount to requestedAmount
                .tenureMonths(response.getTenureMonths())
                .status(response.getStatus() != null ? response.getStatus().name() : null)
                .employmentStatus(response.getEmploymentStatus() != null ? response.getEmploymentStatus().name() : null)
                .monthlyIncome(response.getMonthlyIncome())
                .purpose(response.getPurpose())
                .appliedAt(response.getAppliedDate())
                .createdAt(response.getCreatedAt())
                .updatedAt(response.getUpdatedAt())
                .build();

        return ResponseEntity.ok(loanDTO);
    }

    /**
     * Get total loans count (for reporting service)
     */
    @GetMapping("/count")
    public ResponseEntity<Long> getTotalLoans() {
        log.debug("Internal API: Fetching total loans count");
        List<LoanResponse> allLoans = loanApplicationService.getAllLoans();
        return ResponseEntity.ok((long) allLoans.size());
    }

    /**
     * Get all loans (for reporting service)
     */
    @GetMapping
    public ResponseEntity<List<LoanDTO>> getAllLoans() {
        log.debug("Internal API: Fetching all loans");
        List<LoanResponse> responses = loanApplicationService.getAllLoans();

        List<LoanDTO> loanDTOs = responses.stream()
                .map(this::convertToLoanDTO)
                .collect(java.util.stream.Collectors.toList());

        return ResponseEntity.ok(loanDTOs);
    }

    /**
     * Get recent loans (for reporting service)
     */
    @GetMapping("/recent")
    public ResponseEntity<List<LoanDTO>> getRecentLoans(@RequestParam(defaultValue = "10") int limit) {
        log.debug("Internal API: Fetching recent {} loans", limit);
        List<LoanResponse> responses = loanApplicationService.getAllLoans();

        // Sort by creation date descending and limit
        List<LoanDTO> loanDTOs = responses.stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(limit)
                .map(this::convertToLoanDTO)
                .collect(java.util.stream.Collectors.toList());

        return ResponseEntity.ok(loanDTOs);
    }

    /**
     * Get loans by status count (for reporting service)
     */
    @GetMapping("/status/{status}/count")
    public ResponseEntity<Long> getLoansByStatusCount(@PathVariable String status) {
        log.debug("Internal API: Fetching count of loans with status: {}", status);
        List<LoanResponse> responses = loanApplicationService.getLoansByStatus(status);
        return ResponseEntity.ok((long) responses.size());
    }

    /**
     * Get loans by officer ID (for reporting service)
     */
    @GetMapping("/officer/{officerId}")
    public ResponseEntity<List<LoanDTO>> getLoansByOfficerId(@PathVariable Long officerId) {
        log.debug("Internal API: Fetching loans for officer ID: {}", officerId);
        List<LoanResponse> responses = loanApplicationService.getLoansByOfficerId(officerId);

        List<LoanDTO> loanDTOs = responses.stream()
                .map(this::convertToLoanDTO)
                .collect(java.util.stream.Collectors.toList());

        return ResponseEntity.ok(loanDTOs);
    }

    /**
     * Get loan statistics (for reporting service)
     */
    @GetMapping("/statistics")
    public ResponseEntity<java.util.Map<String, Object>> getLoanStatistics(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        log.debug("Internal API: Fetching loan statistics");

        List<LoanResponse> allLoans = loanApplicationService.getAllLoans();
        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("totalLoans", allLoans.size());
        stats.put("pendingLoans", loanApplicationService.getLoansByStatus("APPLIED").size());
        stats.put("approvedLoans", loanApplicationService.getLoansByStatus("APPROVED").size());
        stats.put("disbursedLoans", loanApplicationService.getLoansByStatus("DISBURSED").size());
        stats.put("rejectedLoans", loanApplicationService.getLoansByStatus("REJECTED").size());

        return ResponseEntity.ok(stats);
    }

    /**
     * Get loans by customer ID (for other microservices)
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<LoanDTO>> getLoansByCustomerId(@PathVariable Long customerId) {
        log.debug("Internal API: Fetching loans for customer ID: {}", customerId);
        List<LoanResponse> responses = loanApplicationService.getCustomerLoans(customerId);

        List<LoanDTO> loanDTOs = responses.stream()
                .map(this::convertToLoanDTO)
                .collect(java.util.stream.Collectors.toList());

        return ResponseEntity.ok(loanDTOs);
    }

    // Helper method to convert LoanResponse to LoanDTO
    private LoanDTO convertToLoanDTO(LoanResponse response) {
        return LoanDTO.builder()
                .id(response.getId())
                .customerId(response.getCustomerId())
                .loanTypeId(response.getLoanTypeId())
                .loanOfficerId(response.getLoanOfficerId())
                .requestedAmount(response.getAmount())
                .approvedAmount(null)  // Not available in LoanResponse, would need approval service
                .tenureMonths(response.getTenureMonths())
                .interestRate(null)  // Not available in LoanResponse, would need approval service
                .status(response.getStatus() != null ? response.getStatus().name() : null)
                .employmentStatus(response.getEmploymentStatus() != null ? response.getEmploymentStatus().name() : null)
                .monthlyIncome(response.getMonthlyIncome())
                .purpose(response.getPurpose())
                .appliedAt(response.getAppliedDate() != null ? response.getAppliedDate() : response.getAppliedAt())
                .disbursedAt(null)  // Not available in LoanResponse, would need disbursement service
                .createdAt(response.getCreatedAt())
                .updatedAt(response.getUpdatedAt())
                .build();
    }

    /**
     * Get loans by status (for other microservices)
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<LoanResponse>> getLoansByStatus(@PathVariable String status) {
        log.debug("Internal API: Fetching loans with status: {}", status);
        List<LoanResponse> responses = loanApplicationService.getLoansByStatus(status);
        return ResponseEntity.ok(responses);
    }

    /**
     * Update loan status to APPROVED (for loan-approval-service)
     */
    @PutMapping("/{loanId}/approve")
    public ResponseEntity<Void> approveLoan(@PathVariable Long loanId, @RequestBody ApprovalRequest request) {
        log.debug("Internal API: Updating loan {} to APPROVED", loanId);
        loanApplicationService.approveLoan(loanId, request.getApprovedAmount(), request.getInterestRate());
        return ResponseEntity.ok().build();
    }

    /**
     * Update loan status to REJECTED (for loan-approval-service)
     */
    @PutMapping("/{loanId}/reject")
    public ResponseEntity<Void> rejectLoan(@PathVariable Long loanId, @RequestBody RejectionRequest request) {
        log.debug("Internal API: Updating loan {} to REJECTED", loanId);
        loanApplicationService.rejectLoan(loanId, request.getReason());
        return ResponseEntity.ok().build();
    }

    /**
     * Update loan status to DISBURSED (for loan-approval-service)
     */
    @PutMapping("/{loanId}/disburse")
    public ResponseEntity<Void> disburseLoan(@PathVariable Long loanId, @RequestBody DisbursementRequest request) {
        log.debug("Internal API: Updating loan {} to DISBURSED", loanId);
        loanApplicationService.disburseLoan(loanId, request.getDisbursementDate(), request.getDisbursementMethod(), request.getReferenceNumber());
        return ResponseEntity.ok().build();
    }

    /**
     * Update loan status to CLOSED (for loan-approval-service)
     */
    @PutMapping("/{loanId}/close")
    public ResponseEntity<Void> closeLoan(@PathVariable Long loanId) {
        log.debug("Internal API: Updating loan {} to CLOSED", loanId);
        loanApplicationService.closeLoan(loanId);
        return ResponseEntity.ok().build();
    }

    // Inner classes for request bodies
    @lombok.Data
    static class ApprovalRequest {
        private java.math.BigDecimal approvedAmount;
        private java.math.BigDecimal interestRate;
    }

    @lombok.Data
    static class RejectionRequest {
        private String reason;
    }

    @lombok.Data
    static class DisbursementRequest {
        private java.time.LocalDate disbursementDate;
        private String disbursementMethod;
        private String referenceNumber;
    }
}
