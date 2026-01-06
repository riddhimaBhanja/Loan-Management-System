package com.loanmanagement.reporting.domain.service;

import com.loanmanagement.reporting.application.dto.response.CustomerSummaryResponse;

/**
 * Service interface for customer reports
 */
public interface CustomerReportService {

    /**
     * Get customer summary with loan and payment details
     */
    CustomerSummaryResponse getCustomerSummary(Long customerId);
}
