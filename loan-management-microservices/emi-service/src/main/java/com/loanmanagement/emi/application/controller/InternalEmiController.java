package com.loanmanagement.emi.application.controller;

import com.loanmanagement.common.dto.GenerateEmiRequest;
import com.loanmanagement.emi.application.dto.response.EmiScheduleResponse;
import com.loanmanagement.emi.domain.service.EmiScheduleService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Internal REST Controller for inter-service communication
 * These endpoints are called by other microservices and bypass authentication
 */
@RestController
@RequestMapping("/api/internal/emis")
public class InternalEmiController {

    private static final Logger logger = LoggerFactory.getLogger(InternalEmiController.class);

    @Autowired
    private EmiScheduleService emiScheduleService;

    /**
     * Generate EMI schedule for a disbursed loan
     * Called by loan-approval-service after disbursement
     */
    @PostMapping("/generate")
    public ResponseEntity<List<EmiScheduleResponse>> generateEmiSchedule(
            @Valid @RequestBody GenerateEmiRequest request) {
        logger.info("Internal API: Generating EMI schedule for loan ID: {}", request.getLoanId());

        List<EmiScheduleResponse> responses = emiScheduleService.generateEmiSchedule(request);

        logger.info("Internal API: Successfully generated {} EMI schedules for loan ID: {}",
                responses.size(), request.getLoanId());

        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }

    /**
     * Check if all EMIs are paid for a loan
     * Called by loan-approval-service for loan closure
     */
    @GetMapping("/loan/{loanId}/all-paid")
    public ResponseEntity<Boolean> areAllEmisPaid(@PathVariable Long loanId) {
        logger.info("Internal API: Checking if all EMIs are paid for loan ID: {}", loanId);

        boolean allPaid = emiScheduleService.verifyAllEmisPaid(loanId);

        return ResponseEntity.ok(allPaid);
    }

    /**
     * Get outstanding amount for a loan
     * Called by loan-approval-service or reporting-service
     */
    @GetMapping("/loan/{loanId}/outstanding")
    public ResponseEntity<BigDecimal> getOutstandingAmount(@PathVariable Long loanId) {
        logger.info("Internal API: Fetching outstanding amount for loan ID: {}", loanId);

        BigDecimal outstanding = emiScheduleService.getOutstandingAmount(loanId);

        return ResponseEntity.ok(outstanding);
    }

    /**
     * Trigger overdue EMI marking (for scheduled jobs or manual trigger)
     */
    @GetMapping("/mark-overdue")
    public ResponseEntity<Integer> markOverdueEmis() {
        logger.info("Internal API: Manually triggering overdue EMI marking");

        int count = emiScheduleService.markOverdueEmis();

        return ResponseEntity.ok(count);
    }

    /**
     * Get EMI schedule for a loan
     * Called by reporting-service
     */
    @GetMapping("/loan/{loanId}")
    public ResponseEntity<List<EmiScheduleResponse>> getEmisByLoanId(@PathVariable Long loanId) {
        logger.info("Internal API: Fetching EMI schedules for loan ID: {}", loanId);

        List<EmiScheduleResponse> emis = emiScheduleService.getEmiSchedule(loanId);

        return ResponseEntity.ok(emis);
    }

    /**
     * Get EMI schedules for a customer
     * Called by reporting-service
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<EmiScheduleResponse>> getEmisByCustomerId(@PathVariable Long customerId) {
        logger.info("Internal API: Fetching EMI schedules for customer ID: {}", customerId);

        List<EmiScheduleResponse> emis = emiScheduleService.getEmiScheduleByCustomer(customerId);

        return ResponseEntity.ok(emis);
    }

    /**
     * Get total EMI collected across all loans
     * Called by reporting-service for dashboard statistics
     */
    @GetMapping("/statistics/total-collected")
    public ResponseEntity<BigDecimal> getTotalCollected() {
        logger.info("Internal API: Fetching total EMI collected");

        BigDecimal totalCollected = emiScheduleService.getTotalCollected();

        return ResponseEntity.ok(totalCollected);
    }

    /**
     * Get total pending EMI amount
     * Called by reporting-service for dashboard statistics
     */
    @GetMapping("/statistics/pending-amount")
    public ResponseEntity<BigDecimal> getPendingAmount() {
        logger.info("Internal API: Fetching total pending EMI amount");

        BigDecimal pendingAmount = emiScheduleService.getTotalPending();

        return ResponseEntity.ok(pendingAmount);
    }

    /**
     * Get overdue statistics
     * Called by reporting-service for dashboard
     */
    @GetMapping("/statistics/overdue")
    public ResponseEntity<java.util.Map<String, Object>> getOverdueStatistics() {
        logger.info("Internal API: Fetching overdue statistics");

        java.util.Map<String, Object> stats = emiScheduleService.getOverdueStatistics();

        return ResponseEntity.ok(stats);
    }

    /**
     * Get payment statistics for a date range
     * Called by reporting-service
     */
    @GetMapping("/statistics/payment")
    public ResponseEntity<java.util.Map<String, Object>> getPaymentStatistics(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        logger.info("Internal API: Fetching payment statistics from {} to {}", startDate, endDate);

        java.util.Map<String, Object> stats = emiScheduleService.getPaymentStatistics(startDate, endDate);

        return ResponseEntity.ok(stats);
    }

    /**
     * Get customer EMI summary including next EMI due date and amount
     * Called by reporting-service for customer dashboard
     * This is the CRITICAL endpoint for displaying "Next EMI Payment" card
     */
    @GetMapping("/statistics/customer/{customerId}/summary")
    public ResponseEntity<java.util.Map<String, Object>> getCustomerEmiSummary(@PathVariable Long customerId) {
        logger.info("Internal API: Fetching EMI summary for customer ID: {}", customerId);

        java.util.Map<String, Object> summary = emiScheduleService.getCustomerEmiSummary(customerId);

        return ResponseEntity.ok(summary);
    }

    /**
     * Get upcoming EMIs due within next N days
     * Called by notification-service for sending due reminders
     */
    @GetMapping("/upcoming")
    public ResponseEntity<List<EmiScheduleResponse>> getUpcomingEmis(
            @RequestParam(defaultValue = "3") Integer daysAhead) {
        logger.info("Internal API: Fetching EMIs due within next {} days", daysAhead);

        List<EmiScheduleResponse> upcomingEmis = emiScheduleService.getUpcomingEmis(daysAhead);

        logger.info("Internal API: Found {} upcoming EMIs", upcomingEmis.size());
        return ResponseEntity.ok(upcomingEmis);
    }
}
