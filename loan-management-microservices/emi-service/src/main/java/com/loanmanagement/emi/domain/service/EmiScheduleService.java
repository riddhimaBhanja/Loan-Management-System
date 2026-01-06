package com.loanmanagement.emi.domain.service;

import com.loanmanagement.common.dto.GenerateEmiRequest;
import com.loanmanagement.emi.application.dto.response.EmiScheduleResponse;
import com.loanmanagement.emi.application.dto.response.EmiSummaryResponse;

import java.util.List;

/**
 * Service interface for EMI Schedule management
 */
public interface EmiScheduleService {

    /**
     * Generate EMI schedule for a disbursed loan
     *
     * @param request Generate EMI request
     * @return List of generated EMI schedules
     */
    List<EmiScheduleResponse> generateEmiSchedule(GenerateEmiRequest request);

    /**
     * Get EMI schedule for a loan
     *
     * @param loanId Loan ID
     * @return List of EMI schedules
     */
    List<EmiScheduleResponse> getEmiSchedule(Long loanId);

    /**
     * Get EMI schedule for a customer
     *
     * @param customerId Customer ID
     * @return List of EMI schedules for all customer loans
     */
    List<EmiScheduleResponse> getEmiScheduleByCustomer(Long customerId);

    /**
     * Get EMI schedule for the currently logged-in customer
     *
     * @return List of EMI schedules for all current customer's loans
     */
    List<EmiScheduleResponse> getMyEmiSchedule();

    /**
     * Get pending EMIs for the currently logged-in customer
     *
     * @return List of pending EMI schedules
     */
    List<EmiScheduleResponse> getPendingEmisForCurrentUser();

    /**
     * Get EMI summary for a loan
     *
     * @param loanId Loan ID
     * @return EMI summary with paid/pending/overdue details
     */
    EmiSummaryResponse getEmiSummary(Long loanId);

    /**
     * Get overdue EMIs
     *
     * @return List of overdue EMI schedules
     */
    List<EmiScheduleResponse> getOverdueEmis();

    /**
     * Get overdue EMIs for a customer
     *
     * @param customerId Customer ID
     * @return List of overdue EMI schedules
     */
    List<EmiScheduleResponse> getOverdueEmisByCustomer(Long customerId);

    /**
     * Update overdue EMI statuses (scheduled job)
     *
     * @return Count of EMIs marked as overdue
     */
    int markOverdueEmis();

    /**
     * Check if all EMIs are paid for a loan
     *
     * @param loanId Loan ID
     * @return true if all EMIs paid, false otherwise
     */
    boolean verifyAllEmisPaid(Long loanId);

    /**
     * Get total outstanding amount for a loan
     *
     * @param loanId Loan ID
     * @return Outstanding amount
     */
    java.math.BigDecimal getOutstandingAmount(Long loanId);

    /**
     * Get total collected EMI amount across all loans
     *
     * @return Total collected amount
     */
    java.math.BigDecimal getTotalCollected();

    /**
     * Get total pending EMI amount across all loans
     *
     * @return Total pending amount
     */
    java.math.BigDecimal getTotalPending();

    /**
     * Get overdue statistics including count and amount
     *
     * @return Map with overdue count and amount
     */
    java.util.Map<String, Object> getOverdueStatistics();

    /**
     * Get payment statistics for a date range
     *
     * @param startDate Start date (optional)
     * @param endDate End date (optional)
     * @return Map with payment statistics
     */
    java.util.Map<String, Object> getPaymentStatistics(String startDate, String endDate);

    /**
     * Get customer EMI summary including next EMI due date and amount
     * This is critical for dashboard "Next EMI Payment" card
     *
     * @param customerId Customer ID
     * @return Map with customer EMI summary
     */
    java.util.Map<String, Object> getCustomerEmiSummary(Long customerId);

    /**
     * Get upcoming EMIs due within next N days
     * Used by notification service for sending due reminders
     *
     * @param daysAhead Number of days ahead to check
     * @return List of upcoming EMI schedules
     */
    List<EmiScheduleResponse> getUpcomingEmis(Integer daysAhead);
}
