package com.loanmanagement.loanapproval.infrastructure.client;

import com.loanmanagement.common.dto.LoanDTO;
import com.loanmanagement.common.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Client for communicating with Loan Application Service
 */
@Component
public class LoanApplicationServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(LoanApplicationServiceClient.class);

    private final RestTemplate restTemplate;
    private final String loanServiceUrl;

    public LoanApplicationServiceClient(
            RestTemplate restTemplate,
            @Value("${services.loan-application-service.url}") String loanServiceUrl) {
        this.restTemplate = restTemplate;
        this.loanServiceUrl = loanServiceUrl;
    }

    /**
     * Get loan details by ID
     */
    public LoanDTO getLoanById(Long loanId) {
        logger.info("Fetching loan details for loan ID: {}", loanId);

        String url = loanServiceUrl + "/api/internal/loans/" + loanId;

        try {
            ResponseEntity<LoanDTO> response = restTemplate.getForEntity(url, LoanDTO.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                logger.info("Successfully fetched loan details for loan ID: {}", loanId);
                return response.getBody();
            }

            throw new ResourceNotFoundException("Loan not found with ID: " + loanId);
        } catch (Exception e) {
            logger.error("Error fetching loan details for loan ID: {}", loanId, e);
            throw new ResourceNotFoundException("Loan not found with ID: " + loanId);
        }
    }

    /**
     * Update loan status to APPROVED
     */
    public void updateLoanStatusToApproved(Long loanId, BigDecimal approvedAmount, BigDecimal interestRate) {
        logger.info("Updating loan {} status to APPROVED", loanId);

        String url = loanServiceUrl + "/api/internal/loans/" + loanId + "/approve";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestBody = String.format(
            "{\"approvedAmount\": %s, \"interestRate\": %s}",
            approvedAmount, interestRate
        );

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        try {
            restTemplate.put(url, request);
            logger.info("Successfully updated loan {} status to APPROVED", loanId);
        } catch (Exception e) {
            logger.error("Error updating loan {} status to APPROVED", loanId, e);
            throw new RuntimeException("Failed to update loan status to APPROVED: " + e.getMessage());
        }
    }

    /**
     * Update loan status to REJECTED
     */
    public void updateLoanStatusToRejected(Long loanId, String reason) {
        logger.info("Updating loan {} status to REJECTED", loanId);

        String url = loanServiceUrl + "/api/internal/loans/" + loanId + "/reject";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestBody = String.format("{\"reason\": \"%s\"}", reason);

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        try {
            restTemplate.put(url, request);
            logger.info("Successfully updated loan {} status to REJECTED", loanId);
        } catch (Exception e) {
            logger.error("Error updating loan {} status to REJECTED", loanId, e);
            throw new RuntimeException("Failed to update loan status to REJECTED: " + e.getMessage());
        }
    }

    /**
     * Update loan status to DISBURSED
     */
    public void updateLoanStatusToDisbursed(Long loanId, LocalDate disbursementDate, String method, String reference) {
        logger.info("Updating loan {} status to DISBURSED", loanId);

        String url = loanServiceUrl + "/api/internal/loans/" + loanId + "/disburse";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestBody = String.format(
            "{\"disbursementDate\": \"%s\", \"disbursementMethod\": \"%s\", \"referenceNumber\": \"%s\"}",
            disbursementDate, method, reference
        );

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        try {
            restTemplate.put(url, request);
            logger.info("Successfully updated loan {} status to DISBURSED", loanId);
        } catch (Exception e) {
            logger.error("Error updating loan {} status to DISBURSED", loanId, e);
            throw new RuntimeException("Failed to update loan status to DISBURSED: " + e.getMessage());
        }
    }

    /**
     * Update loan status to CLOSED
     */
    public void updateLoanStatusToClosed(Long loanId) {
        logger.info("Updating loan {} status to CLOSED", loanId);

        String url = loanServiceUrl + "/api/internal/loans/" + loanId + "/close";

        try {
            restTemplate.put(url, null);
            logger.info("Successfully updated loan {} status to CLOSED", loanId);
        } catch (Exception e) {
            logger.error("Error updating loan {} status to CLOSED", loanId, e);
            throw new RuntimeException("Failed to update loan status to CLOSED: " + e.getMessage());
        }
    }

    /**
     * Get all loans (for statistics)
     */
    public java.util.List<LoanDTO> getAllLoans() {
        logger.info("Fetching all loans");

        String url = loanServiceUrl + "/api/internal/loans";

        try {
            ResponseEntity<LoanDTO[]> response = restTemplate.getForEntity(url, LoanDTO[].class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                logger.info("Successfully fetched all loans");
                return java.util.Arrays.asList(response.getBody());
            }

            return new java.util.ArrayList<>();
        } catch (Exception e) {
            logger.error("Error fetching all loans", e);
            return new java.util.ArrayList<>();
        }
    }
}
