package com.loanmanagement.reporting.application.controller;

import com.loanmanagement.reporting.application.dto.response.LoanStatisticsResponse;
import com.loanmanagement.reporting.application.dto.response.PaymentStatisticsResponse;
import com.loanmanagement.reporting.application.dto.response.UserStatisticsResponse;
import com.loanmanagement.reporting.domain.service.AdminReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * REST Controller for admin report endpoints
 */
@RestController
@RequestMapping("/api/reports/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminReportController {

    private static final Logger logger = LoggerFactory.getLogger(AdminReportController.class);

    @Autowired
    private AdminReportService adminReportService;

    /**
     * Get loan statistics
     */
    @GetMapping("/loans/statistics")
    public ResponseEntity<LoanStatisticsResponse> getLoanStatistics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        logger.info("Fetching loan statistics from {} to {}", startDate, endDate);
        LoanStatisticsResponse response = adminReportService.getLoanStatistics(startDate, endDate);
        return ResponseEntity.ok(response);
    }

    /**
     * Get payment statistics
     */
    @GetMapping("/payments/statistics")
    public ResponseEntity<PaymentStatisticsResponse> getPaymentStatistics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        logger.info("Fetching payment statistics from {} to {}", startDate, endDate);
        PaymentStatisticsResponse response = adminReportService.getPaymentStatistics(startDate, endDate);
        return ResponseEntity.ok(response);
    }

    /**
     * Get user statistics
     */
    @GetMapping("/users/statistics")
    public ResponseEntity<UserStatisticsResponse> getUserStatistics() {
        logger.info("Fetching user statistics");
        UserStatisticsResponse response = adminReportService.getUserStatistics();
        return ResponseEntity.ok(response);
    }
}
