package com.loanmanagement.reporting.domain.service;
import com.loanmanagement.common.dto.LoanDTO;
import com.loanmanagement.common.dto.UserDetailsDTO;
import com.loanmanagement.reporting.application.dto.response.DashboardResponse;
import com.loanmanagement.reporting.application.dto.response.LoanSummary;
import com.loanmanagement.reporting.infrastructure.client.EmiServiceClient;
import com.loanmanagement.reporting.infrastructure.client.LoanApplicationServiceClient;
import com.loanmanagement.reporting.infrastructure.client.LoanApprovalServiceClient;
import com.loanmanagement.reporting.infrastructure.client.UserServiceClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation of DashboardService
 */
@Service
public class DashboardServiceImpl implements DashboardService {

    private static final Logger logger = LoggerFactory.getLogger(DashboardServiceImpl.class);

    @Autowired
    private LoanApplicationServiceClient loanApplicationClient;

    @Autowired
    private LoanApprovalServiceClient loanApprovalClient;

    @Autowired
    private EmiServiceClient emiServiceClient;

    @Autowired
    private UserServiceClient userServiceClient;

    @Override
    @CircuitBreaker(name = "loanApplicationService", fallbackMethod = "getAdminDashboardFallback")
    public DashboardResponse getAdminDashboard() {
        logger.info("Fetching admin dashboard");

        try {
            // Get loan statistics
            logger.debug("Calling loanApplicationClient.getTotalLoans()");
            Long totalLoans = loanApplicationClient.getTotalLoans();
            logger.debug("Total loans: {}", totalLoans);

            logger.debug("Calling loanApprovalClient.getPendingApprovalsCount()");
            Long pendingApprovals = loanApprovalClient.getPendingApprovalsCount();
            logger.debug("Pending approvals: {}", pendingApprovals);

            logger.debug("Calling loanApprovalClient.getApprovedLoansCount()");
            Long approvedLoans = loanApprovalClient.getApprovedLoansCount();
            logger.debug("Approved loans: {}", approvedLoans);

            logger.debug("Calling loanApplicationClient.getLoansByStatus(DISBURSED)");
            Long disbursedLoans = loanApplicationClient.getLoansByStatus("DISBURSED");
            logger.debug("Disbursed loans: {}", disbursedLoans);

            // Get recent loans
            logger.debug("Calling loanApplicationClient.getRecentLoans(5)");
            List<LoanDTO> recentLoanDTOs = loanApplicationClient.getRecentLoans(5);
            List<LoanSummary> recentLoans = convertToLoanSummaries(recentLoanDTOs);
            logger.debug("Recent loans count: {}", recentLoans.size());

            // Get EMI statistics
            logger.debug("Calling emiServiceClient.getOverdueStatistics()");
            Map<String, Object> overdueStats = emiServiceClient.getOverdueStatistics();
            logger.debug("Calling emiServiceClient.getTotalCollected()");
            BigDecimal totalEmiCollected = emiServiceClient.getTotalCollected();
            logger.debug("Calling emiServiceClient.getPendingAmount()");
            BigDecimal pendingEmiAmount = emiServiceClient.getPendingAmount();

            // Extract overdue information
            BigDecimal overdueAmount = overdueStats.containsKey("overdueAmount") ?
                new BigDecimal(overdueStats.get("overdueAmount").toString()) : BigDecimal.ZERO;
            Integer overdueCount = overdueStats.containsKey("overdueCount") ?
                Integer.valueOf(overdueStats.get("overdueCount").toString()) : 0;

            // Get user statistics
            logger.debug("Calling userServiceClient.getTotalUsers()");
            Long totalUsers = userServiceClient.getTotalUsers();
            logger.debug("Total users: {}", totalUsers);

            logger.debug("Calling userServiceClient.getUsersByRole(CUSTOMER)");
            Long totalCustomers = userServiceClient.getUsersByRole("CUSTOMER");
            logger.debug("Total customers: {}", totalCustomers);

            // Calculate total disbursed amount
            logger.debug("Calling loanApplicationClient.getAllLoans()");
            List<LoanDTO> allLoans = loanApplicationClient.getAllLoans();
            BigDecimal totalDisbursedAmount = allLoans.stream()
                .filter(loan -> "DISBURSED".equals(loan.getStatus()) || "APPROVED".equals(loan.getStatus()))
                .map(LoanDTO::getApprovedAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            logger.debug("Total disbursed amount: {}", totalDisbursedAmount);

            logger.info("Successfully fetched admin dashboard data");
            return DashboardResponse.builder()
                .totalLoans(totalLoans)
                .totalCustomers(totalCustomers)
                .pendingApprovals(pendingApprovals)
                .approvedLoans(approvedLoans)
                .disbursedLoans(disbursedLoans)
                .totalDisbursedAmount(totalDisbursedAmount)
                .totalEmiCollected(totalEmiCollected)
                .pendingEmiAmount(pendingEmiAmount)
                .overdueAmount(overdueAmount)
                .overdueCount(overdueCount)
                .recentLoans(recentLoans)
                .totalUsers(totalUsers)
                .build();

        } catch (Exception e) {
            logger.error("Error fetching admin dashboard: {} - {}", e.getClass().getSimpleName(), e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @CircuitBreaker(name = "loanApplicationService", fallbackMethod = "getOfficerDashboardFallback")
    public DashboardResponse getOfficerDashboard(Long officerId) {
        logger.info("Fetching officer dashboard for officer: {}", officerId);

        try {
            // Get officer's assigned loans
            List<LoanDTO> assignedLoans = loanApplicationClient.getLoansByOfficerId(officerId);

            // Count by status
            Long totalLoans = (long) assignedLoans.size();
            Long approved = assignedLoans.stream()
                .filter(l -> "APPROVED".equals(l.getStatus()) || "DISBURSED".equals(l.getStatus()))
                .count();

            // Get pending approvals for this officer
            Long pendingApprovals = loanApprovalClient.getPendingApprovalsByOfficerId(officerId);

            // Get recent assigned loans
            List<LoanSummary> recentLoans = assignedLoans.stream()
                .limit(10)
                .map(this::convertToLoanSummary)
                .collect(Collectors.toList());

            // Get overdue EMIs for assigned loans
            Map<String, Object> overdueStats = emiServiceClient.getOverdueStatistics();
            Integer overdueCount = overdueStats.containsKey("overdueCount") ?
                Integer.valueOf(overdueStats.get("overdueCount").toString()) : 0;

            return DashboardResponse.builder()
                .totalLoans(totalLoans)
                .pendingApprovals(pendingApprovals)
                .approvedLoans(approved)
                .overdueCount(overdueCount)
                .recentLoans(recentLoans)
                .build();

        } catch (Exception e) {
            logger.error("Error fetching officer dashboard: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    @CircuitBreaker(name = "loanApplicationService", fallbackMethod = "getCustomerDashboardFallback")
    public DashboardResponse getCustomerDashboard(Long customerId) {
        logger.info("Fetching customer dashboard for customer: {}", customerId);

        try {
            // Get customer's loans
            List<LoanDTO> customerLoans = loanApplicationClient.getLoansByCustomerId(customerId);

            // Count loans
            Long totalLoans = (long) customerLoans.size();
            Long activeLoans = customerLoans.stream()
                .filter(l -> "DISBURSED".equals(l.getStatus()))
                .count();
            Long closedLoans = customerLoans.stream()
                .filter(l -> "CLOSED".equals(l.getStatus()))
                .count();
            Long pendingLoans = customerLoans.stream()
                .filter(l -> "APPLIED".equals(l.getStatus()) ||
                           "UNDER_REVIEW".equals(l.getStatus()) ||
                           "APPROVED".equals(l.getStatus()))
                .count();

            // Get customer's EMI summary
            Map<String, Object> emiSummary = emiServiceClient.getCustomerEmiSummary(customerId);

            BigDecimal totalOutstanding = emiSummary.containsKey("totalPending") ?
                new BigDecimal(emiSummary.get("totalPending").toString()) : BigDecimal.ZERO;
            BigDecimal nextEmiAmount = emiSummary.containsKey("nextEmiAmount") ?
                new BigDecimal(emiSummary.get("nextEmiAmount").toString()) : null;
            LocalDate nextEmiDueDate = emiSummary.containsKey("nextEmiDueDate") &&
                emiSummary.get("nextEmiDueDate") != null ?
                LocalDate.parse(emiSummary.get("nextEmiDueDate").toString()) : null;
            Long nextEmiLoanId = emiSummary.containsKey("nextEmiLoanId") ?
                Long.valueOf(emiSummary.get("nextEmiLoanId").toString()) : null;
            Long overdueCount = emiSummary.containsKey("overdueCount") ?
                Long.valueOf(emiSummary.get("overdueCount").toString()) : 0L;

            // Map customer loans
            List<LoanSummary> myLoans = customerLoans.stream()
                .map(this::convertToLoanSummary)
                .collect(Collectors.toList());

            return DashboardResponse.builder()
                .myTotalLoans(totalLoans)
                .myActiveLoans(activeLoans)
                .myClosedLoans(closedLoans)
                .myPendingLoans(pendingLoans)
                .totalOutstanding(totalOutstanding)
                .nextEmiDueDate(nextEmiDueDate)
                .nextEmiAmount(nextEmiAmount)
                .nextEmiLoanId(nextEmiLoanId)
                .overdueCount(overdueCount.intValue())
                .myLoans(myLoans)
                .build();

        } catch (Exception e) {
            logger.error("Error fetching customer dashboard: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Convert LoanDTO to LoanSummary
     */
    private LoanSummary convertToLoanSummary(LoanDTO loanDTO) {
        // Get customer details
        String customerName = "";
        String customerEmail = "";
        try {
            UserDetailsDTO customer = userServiceClient.getUserById(loanDTO.getCustomerId());
            customerName = customer.getFullName();
            customerEmail = customer.getEmail();
        } catch (Exception e) {
            logger.warn("Failed to fetch customer details for loan: {}", loanDTO.getId());
        }

        return LoanSummary.builder()
            .id(loanDTO.getId())
            .loanType(loanDTO.getLoanTypeId() != null ? loanDTO.getLoanTypeId().toString() : "N/A")
            .requestedAmount(loanDTO.getRequestedAmount())
            .approvedAmount(loanDTO.getApprovedAmount())
            .status(loanDTO.getStatus())
            .customerName(customerName)
            .customerEmail(customerEmail)
            .appliedAt(loanDTO.getAppliedAt())
            .disbursementDate(loanDTO.getDisbursedAt() != null ?
                loanDTO.getDisbursedAt().toLocalDate() : null)
            .tenureMonths(loanDTO.getTenureMonths())
            .interestRate(loanDTO.getInterestRate())
            .build();
    }

    /**
     * Convert list of LoanDTO to list of LoanSummary
     */
    private List<LoanSummary> convertToLoanSummaries(List<LoanDTO> loanDTOs) {
        if (loanDTOs == null || loanDTOs.isEmpty()) {
            return new ArrayList<>();
        }
        return loanDTOs.stream()
            .map(this::convertToLoanSummary)
            .collect(Collectors.toList());
    }

    /**
     * Fallback method for admin dashboard
     */
    public DashboardResponse getAdminDashboardFallback(Exception e) {
        logger.error("Fallback triggered for admin dashboard: {}", e.getMessage());
        return DashboardResponse.builder()
            .totalLoans(0L)
            .totalCustomers(0L)
            .pendingApprovals(0L)
            .approvedLoans(0L)
            .disbursedLoans(0L)
            .totalDisbursedAmount(BigDecimal.ZERO)
            .totalEmiCollected(BigDecimal.ZERO)
            .pendingEmiAmount(BigDecimal.ZERO)
            .overdueAmount(BigDecimal.ZERO)
            .overdueCount(0)
            .recentLoans(new ArrayList<>())
            .build();
    }

    /**
     * Fallback method for officer dashboard
     */
    public DashboardResponse getOfficerDashboardFallback(Long officerId, Exception e) {
        logger.error("Fallback triggered for officer dashboard: {}", e.getMessage());
        return DashboardResponse.builder()
            .totalLoans(0L)
            .pendingApprovals(0L)
            .approvedLoans(0L)
            .overdueCount(0)
            .recentLoans(new ArrayList<>())
            .build();
    }

    /**
     * Fallback method for customer dashboard
     */
    public DashboardResponse getCustomerDashboardFallback(Long customerId, Exception e) {
        logger.error("Fallback triggered for customer dashboard: {}", e.getMessage());
        return DashboardResponse.builder()
            .myTotalLoans(0L)
            .myActiveLoans(0L)
            .myClosedLoans(0L)
            .myPendingLoans(0L)
            .totalOutstanding(BigDecimal.ZERO)
            .overdueCount(0)
            .myLoans(new ArrayList<>())
            .build();
    }
}
