package com.loanmanagement.reporting.domain.service;

import com.loanmanagement.common.dto.LoanDTO;
import com.loanmanagement.reporting.application.dto.response.LoanStatisticsResponse;
import com.loanmanagement.reporting.application.dto.response.PaymentStatisticsResponse;
import com.loanmanagement.reporting.application.dto.response.UserStatisticsResponse;
import com.loanmanagement.reporting.infrastructure.client.EmiServiceClient;
import com.loanmanagement.reporting.infrastructure.client.LoanApplicationServiceClient;
import com.loanmanagement.reporting.infrastructure.client.UserServiceClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation of AdminReportService
 */
@Service
public class AdminReportServiceImpl implements AdminReportService {

    private static final Logger logger = LoggerFactory.getLogger(AdminReportServiceImpl.class);

    @Autowired
    private LoanApplicationServiceClient loanApplicationClient;

    @Autowired
    private EmiServiceClient emiServiceClient;

    @Autowired
    private UserServiceClient userServiceClient;

    @Override
    @CircuitBreaker(name = "loanApplicationService", fallbackMethod = "getLoanStatisticsFallback")
    public LoanStatisticsResponse getLoanStatistics(LocalDate startDate, LocalDate endDate) {
        logger.info("Fetching loan statistics from {} to {}", startDate, endDate);

        try {
            // Get all loans
            List<LoanDTO> allLoans = loanApplicationClient.getAllLoans();

            // Filter by date if provided
            List<LoanDTO> filteredLoans = allLoans;
            if (startDate != null && endDate != null) {
                filteredLoans = allLoans.stream()
                    .filter(loan -> {
                        LocalDate appliedDate = loan.getAppliedAt() != null ?
                            loan.getAppliedAt().toLocalDate() : null;
                        return appliedDate != null &&
                            !appliedDate.isBefore(startDate) &&
                            !appliedDate.isAfter(endDate);
                    })
                    .collect(Collectors.toList());
            }

            // Group by status
            Map<String, Long> loansByStatus = filteredLoans.stream()
                .collect(Collectors.groupingBy(
                    loan -> loan.getStatus() != null ? loan.getStatus() : "UNKNOWN",
                    Collectors.counting()
                ));

            // Group by type (using loanTypeId as type for now)
            Map<String, Long> loansByType = filteredLoans.stream()
                .collect(Collectors.groupingBy(
                    loan -> loan.getLoanTypeId() != null ?
                        "TYPE_" + loan.getLoanTypeId() : "UNKNOWN",
                    Collectors.counting()
                ));

            // Calculate statistics
            Long totalLoans = (long) filteredLoans.size();

            BigDecimal totalRequested = filteredLoans.stream()
                .map(LoanDTO::getRequestedAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal totalDisbursed = filteredLoans.stream()
                .filter(loan -> "DISBURSED".equals(loan.getStatus()) ||
                              "APPROVED".equals(loan.getStatus()))
                .map(LoanDTO::getApprovedAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Calculate average, max, min
            List<BigDecimal> amounts = filteredLoans.stream()
                .map(LoanDTO::getRequestedAmount)
                .filter(amount -> amount != null)
                .collect(Collectors.toList());

            BigDecimal averageLoanAmount = BigDecimal.ZERO;
            BigDecimal maxLoanAmount = BigDecimal.ZERO;
            BigDecimal minLoanAmount = BigDecimal.ZERO;

            if (!amounts.isEmpty()) {
                averageLoanAmount = totalRequested.divide(
                    BigDecimal.valueOf(amounts.size()),
                    2,
                    RoundingMode.HALF_UP
                );
                maxLoanAmount = amounts.stream().max(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
                minLoanAmount = amounts.stream().min(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
            }

            return LoanStatisticsResponse.builder()
                .totalLoans(totalLoans)
                .loansByStatus(loansByStatus)
                .loansByType(loansByType)
                .averageLoanAmount(averageLoanAmount)
                .maxLoanAmount(maxLoanAmount)
                .minLoanAmount(minLoanAmount)
                .totalDisbursed(totalDisbursed)
                .totalRequested(totalRequested)
                .build();

        } catch (Exception e) {
            logger.error("Error fetching loan statistics: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    @CircuitBreaker(name = "emiService", fallbackMethod = "getPaymentStatisticsFallback")
    public PaymentStatisticsResponse getPaymentStatistics(LocalDate startDate, LocalDate endDate) {
        logger.info("Fetching payment statistics from {} to {}", startDate, endDate);

        try {
            // Get payment statistics from EMI service
            String startDateStr = startDate != null ? startDate.toString() : null;
            String endDateStr = endDate != null ? endDate.toString() : null;

            Map<String, Object> paymentStats = emiServiceClient.getPaymentStatistics(
                startDateStr,
                endDateStr
            );

            // Extract statistics
            Long totalPayments = paymentStats.containsKey("totalPayments") ?
                Long.valueOf(paymentStats.get("totalPayments").toString()) : 0L;

            BigDecimal totalCollected = paymentStats.containsKey("totalCollected") ?
                new BigDecimal(paymentStats.get("totalCollected").toString()) : BigDecimal.ZERO;

            BigDecimal totalPending = paymentStats.containsKey("totalPending") ?
                new BigDecimal(paymentStats.get("totalPending").toString()) : BigDecimal.ZERO;

            BigDecimal totalOverdue = paymentStats.containsKey("totalOverdue") ?
                new BigDecimal(paymentStats.get("totalOverdue").toString()) : BigDecimal.ZERO;

            Integer onTimePayments = paymentStats.containsKey("onTimePayments") ?
                Integer.valueOf(paymentStats.get("onTimePayments").toString()) : 0;

            Integer latePayments = paymentStats.containsKey("latePayments") ?
                Integer.valueOf(paymentStats.get("latePayments").toString()) : 0;

            Integer overduePayments = paymentStats.containsKey("overduePayments") ?
                Integer.valueOf(paymentStats.get("overduePayments").toString()) : 0;

            // Calculate average payment
            BigDecimal averagePayment = BigDecimal.ZERO;
            if (totalPayments > 0) {
                averagePayment = totalCollected.divide(
                    BigDecimal.valueOf(totalPayments),
                    2,
                    RoundingMode.HALF_UP
                );
            }

            // Calculate on-time percentage
            Double onTimePercentage = 0.0;
            int totalProcessedPayments = onTimePayments + latePayments;
            if (totalProcessedPayments > 0) {
                onTimePercentage = (onTimePayments * 100.0) / totalProcessedPayments;
            }

            // Collection by method (placeholder - to be implemented in EMI service)
            Map<String, BigDecimal> collectionByMethod = new HashMap<>();
            if (paymentStats.containsKey("collectionByMethod")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> methodMap = (Map<String, Object>) paymentStats.get("collectionByMethod");
                methodMap.forEach((key, value) ->
                    collectionByMethod.put(key, new BigDecimal(value.toString()))
                );
            }

            return PaymentStatisticsResponse.builder()
                .totalPayments(totalPayments)
                .totalCollected(totalCollected)
                .averagePayment(averagePayment)
                .collectionByMethod(collectionByMethod)
                .onTimePayments(onTimePayments)
                .latePayments(latePayments)
                .overduePayments(overduePayments)
                .onTimePercentage(onTimePercentage)
                .totalPending(totalPending)
                .totalOverdue(totalOverdue)
                .build();

        } catch (Exception e) {
            logger.error("Error fetching payment statistics: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    @CircuitBreaker(name = "authService", fallbackMethod = "getUserStatisticsFallback")
    public UserStatisticsResponse getUserStatistics() {
        logger.info("Fetching user statistics");

        try {
            // Get user statistics from Auth service
            Map<String, Object> userStats = userServiceClient.getUserStatistics();

            Long totalUsers = userStats.containsKey("totalUsers") ?
                Long.valueOf(userStats.get("totalUsers").toString()) : 0L;

            Long activeUsers = userStats.containsKey("activeUsers") ?
                Long.valueOf(userStats.get("activeUsers").toString()) : 0L;

            Long inactiveUsers = totalUsers - activeUsers;

            // Get users by role
            Map<String, Long> usersByRole = new HashMap<>();
            if (userStats.containsKey("usersByRole")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> roleMap = (Map<String, Object>) userStats.get("usersByRole");
                roleMap.forEach((key, value) ->
                    usersByRole.put(key, Long.valueOf(value.toString()))
                );
            } else {
                // Fallback: count manually
                usersByRole.put("CUSTOMER", userServiceClient.getUsersByRole("CUSTOMER"));
                usersByRole.put("LOAN_OFFICER", userServiceClient.getUsersByRole("LOAN_OFFICER"));
                usersByRole.put("ADMIN", userServiceClient.getUsersByRole("ADMIN"));
            }

            return UserStatisticsResponse.builder()
                .totalUsers(totalUsers)
                .activeUsers(activeUsers)
                .inactiveUsers(inactiveUsers)
                .usersByRole(usersByRole)
                .build();

        } catch (Exception e) {
            logger.error("Error fetching user statistics: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Fallback method for loan statistics
     */
    public LoanStatisticsResponse getLoanStatisticsFallback(LocalDate startDate, LocalDate endDate, Exception e) {
        logger.error("Fallback triggered for loan statistics: {}", e.getMessage());
        return LoanStatisticsResponse.builder()
            .totalLoans(0L)
            .loansByStatus(new HashMap<>())
            .loansByType(new HashMap<>())
            .averageLoanAmount(BigDecimal.ZERO)
            .maxLoanAmount(BigDecimal.ZERO)
            .minLoanAmount(BigDecimal.ZERO)
            .totalDisbursed(BigDecimal.ZERO)
            .totalRequested(BigDecimal.ZERO)
            .build();
    }

    /**
     * Fallback method for payment statistics
     */
    public PaymentStatisticsResponse getPaymentStatisticsFallback(LocalDate startDate, LocalDate endDate, Exception e) {
        logger.error("Fallback triggered for payment statistics: {}", e.getMessage());
        return PaymentStatisticsResponse.builder()
            .totalPayments(0L)
            .totalCollected(BigDecimal.ZERO)
            .averagePayment(BigDecimal.ZERO)
            .collectionByMethod(new HashMap<>())
            .onTimePayments(0)
            .latePayments(0)
            .overduePayments(0)
            .onTimePercentage(0.0)
            .totalPending(BigDecimal.ZERO)
            .totalOverdue(BigDecimal.ZERO)
            .build();
    }

    /**
     * Fallback method for user statistics
     */
    public UserStatisticsResponse getUserStatisticsFallback(Exception e) {
        logger.error("Fallback triggered for user statistics: {}", e.getMessage());
        return UserStatisticsResponse.builder()
            .totalUsers(0L)
            .activeUsers(0L)
            .inactiveUsers(0L)
            .usersByRole(new HashMap<>())
            .build();
    }
}
