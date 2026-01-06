package com.loanmanagement.reporting.domain.service;

import com.loanmanagement.reporting.application.dto.response.DashboardResponse;

/**
 * Service interface for dashboard operations
 */
public interface DashboardService {

    /**
     * Get admin dashboard with overall system statistics
     */
    DashboardResponse getAdminDashboard();

    /**
     * Get loan officer dashboard with assigned loans
     */
    DashboardResponse getOfficerDashboard(Long officerId);

    /**
     * Get customer dashboard with personal loan information
     */
    DashboardResponse getCustomerDashboard(Long customerId);
}
