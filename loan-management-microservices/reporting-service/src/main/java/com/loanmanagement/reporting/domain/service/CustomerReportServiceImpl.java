package com.loanmanagement.reporting.domain.service;

import com.loanmanagement.common.dto.LoanDTO;
import com.loanmanagement.common.dto.UserDetailsDTO;
import com.loanmanagement.reporting.application.dto.response.CustomerSummaryResponse;
import com.loanmanagement.reporting.application.dto.response.LoanSummary;
import com.loanmanagement.reporting.infrastructure.client.EmiServiceClient;
import com.loanmanagement.reporting.infrastructure.client.LoanApplicationServiceClient;
import com.loanmanagement.reporting.infrastructure.client.UserServiceClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation of CustomerReportService
 */
@Service
public class CustomerReportServiceImpl implements CustomerReportService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerReportServiceImpl.class);

    @Autowired
    private LoanApplicationServiceClient loanApplicationClient;

    @Autowired
    private EmiServiceClient emiServiceClient;

    @Autowired
    private UserServiceClient userServiceClient;

    @Override
    @CircuitBreaker(name = "loanApplicationService", fallbackMethod = "getCustomerSummaryFallback")
    public CustomerSummaryResponse getCustomerSummary(Long customerId) {
        logger.info("Fetching customer summary for customer: {}", customerId);

        try {
            // Get customer details
            UserDetailsDTO customer = userServiceClient.getUserById(customerId);

            // Get customer's loans
            List<LoanDTO> customerLoans = loanApplicationClient.getLoansByCustomerId(customerId);

            // Count loans by status
            long totalLoans = customerLoans.size();
            int activeLoans = (int) customerLoans.stream()
                .filter(l -> "DISBURSED".equals(l.getStatus()))
                .count();
            int closedLoans = (int) customerLoans.stream()
                .filter(l -> "CLOSED".equals(l.getStatus()))
                .count();
            int rejectedLoans = (int) customerLoans.stream()
                .filter(l -> "REJECTED".equals(l.getStatus()))
                .count();

            // Calculate totals
            BigDecimal totalBorrowed = customerLoans.stream()
                .filter(l -> "DISBURSED".equals(l.getStatus()) || "CLOSED".equals(l.getStatus()))
                .map(LoanDTO::getApprovedAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Get EMI summary
            Map<String, Object> emiSummary = emiServiceClient.getCustomerEmiSummary(customerId);

            BigDecimal totalPaid = emiSummary.containsKey("totalPaid") ?
                new BigDecimal(emiSummary.get("totalPaid").toString()) : BigDecimal.ZERO;

            BigDecimal totalPending = emiSummary.containsKey("totalPending") ?
                new BigDecimal(emiSummary.get("totalPending").toString()) : BigDecimal.ZERO;

            // Convert loans to summaries
            List<LoanSummary> loanSummaries = customerLoans.stream()
                .map(this::convertToLoanSummary)
                .collect(Collectors.toList());

            return CustomerSummaryResponse.builder()
                .customerId(customerId)
                .customerName(customer.getFullName())
                .customerEmail(customer.getEmail())
                .totalLoans(totalLoans)
                .totalBorrowed(totalBorrowed)
                .totalPaid(totalPaid)
                .totalPending(totalPending)
                .activeLoans(activeLoans)
                .closedLoans(closedLoans)
                .rejectedLoans(rejectedLoans)
                .loans(loanSummaries)
                .build();

        } catch (Exception e) {
            logger.error("Error fetching customer summary: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Convert LoanDTO to LoanSummary
     */
    private LoanSummary convertToLoanSummary(LoanDTO loanDTO) {
        return LoanSummary.builder()
            .id(loanDTO.getId())
            .loanType(loanDTO.getLoanTypeId() != null ? loanDTO.getLoanTypeId().toString() : "N/A")
            .requestedAmount(loanDTO.getRequestedAmount())
            .approvedAmount(loanDTO.getApprovedAmount())
            .status(loanDTO.getStatus())
            .appliedAt(loanDTO.getAppliedAt())
            .disbursementDate(loanDTO.getDisbursedAt() != null ?
                loanDTO.getDisbursedAt().toLocalDate() : null)
            .tenureMonths(loanDTO.getTenureMonths())
            .interestRate(loanDTO.getInterestRate())
            .build();
    }

    /**
     * Fallback method for customer summary
     */
    public CustomerSummaryResponse getCustomerSummaryFallback(Long customerId, Exception e) {
        logger.error("Fallback triggered for customer summary: {}", e.getMessage());
        return CustomerSummaryResponse.builder()
            .customerId(customerId)
            .customerName("Unknown")
            .customerEmail("Unknown")
            .totalLoans(0L)
            .totalBorrowed(BigDecimal.ZERO)
            .totalPaid(BigDecimal.ZERO)
            .totalPending(BigDecimal.ZERO)
            .activeLoans(0)
            .closedLoans(0)
            .rejectedLoans(0)
            .loans(new ArrayList<>())
            .build();
    }
}
