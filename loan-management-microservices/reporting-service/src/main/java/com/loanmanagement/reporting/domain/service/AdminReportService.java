package com.loanmanagement.reporting.domain.service;

import com.loanmanagement.reporting.application.dto.response.LoanStatisticsResponse;
import com.loanmanagement.reporting.application.dto.response.PaymentStatisticsResponse;
import com.loanmanagement.reporting.application.dto.response.UserStatisticsResponse;

import java.time.LocalDate;

/**
 * Service interface for admin reports
 */
public interface AdminReportService {

    /**
     * Get loan statistics with optional date filters
     */
    LoanStatisticsResponse getLoanStatistics(LocalDate startDate, LocalDate endDate);

    /**
     * Get payment statistics with optional date filters
     */
    PaymentStatisticsResponse getPaymentStatistics(LocalDate startDate, LocalDate endDate);

    /**
     * Get user statistics
     */
    UserStatisticsResponse getUserStatistics();
}
