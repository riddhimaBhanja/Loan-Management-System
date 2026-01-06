package com.loanmanagement.reporting.application.controller;

import com.loanmanagement.reporting.application.dto.response.DashboardResponse;
import com.loanmanagement.reporting.domain.service.DashboardService;
import com.loanmanagement.reporting.infrastructure.security.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for dashboard endpoints
 * Supports both /api/dashboard and /api/reports/dashboard paths
 */
@RestController
public class DashboardController {

    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

    @Autowired
    private DashboardService dashboardService;

    /**
     * Get admin dashboard
     */
    @GetMapping({"/api/dashboard/admin", "/api/reports/dashboard/admin"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DashboardResponse> getAdminDashboard() {
        logger.info("Fetching admin dashboard");
        DashboardResponse response = dashboardService.getAdminDashboard();
        return ResponseEntity.ok(response);
    }

    /**
     * Get loan officer dashboard
     */
    @GetMapping({"/api/dashboard/officer/{officerId}", "/api/reports/dashboard/officer/{officerId}"})
    @PreAuthorize("hasAnyRole('ADMIN', 'LOAN_OFFICER')")
    public ResponseEntity<DashboardResponse> getOfficerDashboard(@PathVariable Long officerId) {
        logger.info("Fetching officer dashboard for officer ID: {}", officerId);
        DashboardResponse response = dashboardService.getOfficerDashboard(officerId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get customer dashboard
     */
    @GetMapping({"/api/dashboard/customer/{customerId}", "/api/reports/dashboard/customer/{customerId}"})
    @PreAuthorize("hasAnyRole('ADMIN', 'LOAN_OFFICER', 'CUSTOMER')")
    public ResponseEntity<DashboardResponse> getCustomerDashboard(@PathVariable Long customerId) {
        logger.info("Fetching customer dashboard for customer ID: {}", customerId);
        DashboardResponse response = dashboardService.getCustomerDashboard(customerId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get current user's dashboard (auto-detect role)
     */
    @GetMapping({"/api/dashboard/me", "/api/reports/dashboard/me"})
    public ResponseEntity<DashboardResponse> getMyDashboard() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long userId = userPrincipal.getUserId();
        String username = userPrincipal.getUsername();

        logger.info("Fetching dashboard for current user: {} (ID: {})", username, userId);

        // Determine user role and return appropriate dashboard
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        boolean isLoanOfficer = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_LOAN_OFFICER"));

        DashboardResponse response;
        if (isAdmin) {
            response = dashboardService.getAdminDashboard();
        } else if (isLoanOfficer) {
            response = dashboardService.getOfficerDashboard(userId);
        } else {
            // Customer
            response = dashboardService.getCustomerDashboard(userId);
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Get dashboard (generic endpoint for all users)
     */
    @GetMapping({"/api/dashboard", "/api/reports/dashboard"})
    public ResponseEntity<DashboardResponse> getDashboard() {
        // Delegate to /me endpoint which handles role-based logic
        return getMyDashboard();
    }
}
