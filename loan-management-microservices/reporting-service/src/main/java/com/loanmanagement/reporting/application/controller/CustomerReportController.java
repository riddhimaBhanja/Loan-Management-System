package com.loanmanagement.reporting.application.controller;

import com.loanmanagement.reporting.application.dto.response.CustomerSummaryResponse;
import com.loanmanagement.reporting.domain.service.CustomerReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for customer report endpoints
 */
@RestController
@RequestMapping("/api/reports/customer")
public class CustomerReportController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerReportController.class);

    @Autowired
    private CustomerReportService customerReportService;

    /**
     * Get customer summary
     */
    @GetMapping("/{customerId}/summary")
    @PreAuthorize("hasAnyRole('ADMIN', 'LOAN_OFFICER', 'CUSTOMER')")
    public ResponseEntity<CustomerSummaryResponse> getCustomerSummary(@PathVariable Long customerId) {
        logger.info("Fetching customer summary for customer ID: {}", customerId);
        CustomerSummaryResponse response = customerReportService.getCustomerSummary(customerId);
        return ResponseEntity.ok(response);
    }
}
