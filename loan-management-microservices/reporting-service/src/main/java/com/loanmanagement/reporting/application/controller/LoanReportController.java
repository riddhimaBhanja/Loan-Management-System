package com.loanmanagement.reporting.application.controller;

import com.loanmanagement.reporting.application.dto.response.LoanStatisticsResponse;
import com.loanmanagement.reporting.domain.service.AdminReportService;
import com.loanmanagement.reporting.infrastructure.security.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * REST Controller for general loan report endpoints
 * Accessible to Admin, Loan Officer, and Customer based on permissions
 */
@RestController
@RequestMapping("/api/reports/loans")
public class LoanReportController {

    private static final Logger logger = LoggerFactory.getLogger(LoanReportController.class);

    @Autowired
    private AdminReportService adminReportService;

    /**
     * Get loan summary/statistics
     * Accessible to all authenticated users
     */
    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('ADMIN', 'LOAN_OFFICER', 'CUSTOMER')")
    public ResponseEntity<LoanStatisticsResponse> getLoanSummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        logger.info("Fetching loan summary for user: {} from {} to {}",
                    userPrincipal.getUsername(), startDate, endDate);

        // All roles can access loan statistics
        // Role-specific filtering is handled in the service layer if needed
        LoanStatisticsResponse response = adminReportService.getLoanStatistics(startDate, endDate);
        return ResponseEntity.ok(response);
    }

    /**
     * Get loan statistics (alias for summary)
     * Accessible to all authenticated users
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'LOAN_OFFICER', 'CUSTOMER')")
    public ResponseEntity<LoanStatisticsResponse> getLoanStatistics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        logger.info("Fetching loan statistics from {} to {}", startDate, endDate);
        LoanStatisticsResponse response = adminReportService.getLoanStatistics(startDate, endDate);
        return ResponseEntity.ok(response);
    }
}
