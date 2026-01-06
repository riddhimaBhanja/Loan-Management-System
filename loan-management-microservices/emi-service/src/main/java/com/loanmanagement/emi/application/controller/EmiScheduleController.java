package com.loanmanagement.emi.application.controller;

import com.loanmanagement.common.dto.ApiResponse;
import com.loanmanagement.common.dto.GenerateEmiRequest;
import com.loanmanagement.emi.application.dto.response.EmiScheduleResponse;
import com.loanmanagement.emi.application.dto.response.EmiSummaryResponse;
import com.loanmanagement.emi.domain.service.EmiScheduleService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for EMI Schedule operations
 */
@RestController
@RequestMapping("/api/emis")
public class EmiScheduleController {

    private static final Logger logger = LoggerFactory.getLogger(EmiScheduleController.class);

    @Autowired
    private EmiScheduleService emiScheduleService;

    /**
     * Generate EMI schedule for a loan
     * Called by loan-approval-service after disbursement
     */
    @PostMapping("/generate")
    @PreAuthorize("hasAnyRole('LOAN_OFFICER', 'ADMIN')")
    public ResponseEntity<List<EmiScheduleResponse>> generateEmiSchedule(
            @Valid @RequestBody GenerateEmiRequest request) {
        logger.info("Received request to generate EMI schedule for loan ID: {}", request.getLoanId());

        List<EmiScheduleResponse> responses = emiScheduleService.generateEmiSchedule(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }

    /**
     * Get EMI schedule for a loan
     */
    @GetMapping("/loan/{loanId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'LOAN_OFFICER', 'ADMIN')")
    public ResponseEntity<ApiResponse> getEmiSchedule(@PathVariable Long loanId) {
        logger.info("Fetching EMI schedule for loan ID: {}", loanId);

        List<EmiScheduleResponse> responses = emiScheduleService.getEmiSchedule(loanId);

        return ResponseEntity.ok(ApiResponse.success("EMI schedule retrieved successfully", responses));
    }

    /**
     * Get EMI summary for a loan
     */
    @GetMapping("/loan/{loanId}/summary")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'LOAN_OFFICER', 'ADMIN')")
    public ResponseEntity<EmiSummaryResponse> getEmiSummary(@PathVariable Long loanId) {
        logger.info("Fetching EMI summary for loan ID: {}", loanId);

        EmiSummaryResponse response = emiScheduleService.getEmiSummary(loanId);

        return ResponseEntity.ok(response);
    }

    /**
     * Get all EMIs for a customer
     */
    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'LOAN_OFFICER', 'ADMIN')")
    public ResponseEntity<List<EmiScheduleResponse>> getCustomerEmis(@PathVariable Long customerId) {
        logger.info("Fetching EMIs for customer ID: {}", customerId);

        List<EmiScheduleResponse> responses = emiScheduleService.getEmiScheduleByCustomer(customerId);

        return ResponseEntity.ok(responses);
    }

    /**
     * Get EMI schedule for the currently logged-in customer
     */
    @GetMapping("/my-schedule")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse> getMyEmiSchedule() {
        logger.info("Fetching EMI schedule for currently logged-in customer");

        List<EmiScheduleResponse> responses = emiScheduleService.getMyEmiSchedule();

        return ResponseEntity.ok(ApiResponse.success("EMI schedules retrieved successfully", responses));
    }

    /**
     * Get pending EMIs for the currently logged-in customer
     */
    @GetMapping("/pending")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse> getPendingEmis() {
        logger.info("Fetching pending EMIs for currently logged-in customer");

        List<EmiScheduleResponse> responses = emiScheduleService.getPendingEmisForCurrentUser();

        return ResponseEntity.ok(ApiResponse.success("Pending EMIs retrieved successfully", responses));
    }

    /**
     * Get overdue EMIs
     */
    @GetMapping("/overdue")
    @PreAuthorize("hasAnyRole('LOAN_OFFICER', 'ADMIN')")
    public ResponseEntity<ApiResponse> getOverdueEmis() {
        logger.info("Fetching overdue EMIs");

        List<EmiScheduleResponse> responses = emiScheduleService.getOverdueEmis();

        return ResponseEntity.ok(ApiResponse.success("Overdue EMIs retrieved successfully", responses));
    }

    /**
     * Get overdue EMIs for a customer
     */
    @GetMapping("/customer/{customerId}/overdue")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'LOAN_OFFICER', 'ADMIN')")
    public ResponseEntity<List<EmiScheduleResponse>> getCustomerOverdueEmis(@PathVariable Long customerId) {
        logger.info("Fetching overdue EMIs for customer ID: {}", customerId);

        List<EmiScheduleResponse> responses = emiScheduleService.getOverdueEmisByCustomer(customerId);

        return ResponseEntity.ok(responses);
    }
}
