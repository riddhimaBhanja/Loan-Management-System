package com.loanmanagement.loanapproval.infrastructure.client;

import com.loanmanagement.common.dto.GenerateEmiRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Client for communicating with EMI Service
 */
@Component
public class EmiServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(EmiServiceClient.class);

    private final RestTemplate restTemplate;
    private final String emiServiceUrl;

    public EmiServiceClient(
            RestTemplate restTemplate,
            @Value("${services.emi-service.url}") String emiServiceUrl) {
        this.restTemplate = restTemplate;
        this.emiServiceUrl = emiServiceUrl;
    }

    /**
     * Generate EMI schedule for a disbursed loan
     */
    public void generateEmiSchedule(Long loanId, Long customerId, BigDecimal principal,
                                   BigDecimal interestRate, Integer tenureMonths, LocalDate disbursementDate) {
        logger.info("Requesting EMI schedule generation for loan ID: {}", loanId);

        String url = emiServiceUrl + "/api/internal/emis/generate";

        GenerateEmiRequest request = GenerateEmiRequest.builder()
                .loanId(loanId)
                .customerId(customerId)
                .principal(principal)
                .interestRate(interestRate)
                .tenureMonths(tenureMonths)
                .startDate(disbursementDate)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<GenerateEmiRequest> httpRequest = new HttpEntity<>(request, headers);

        try {
            restTemplate.postForEntity(url, httpRequest, Void.class);
            logger.info("Successfully requested EMI schedule generation for loan ID: {}", loanId);
        } catch (Exception e) {
            logger.error("Error generating EMI schedule for loan ID: {}", loanId, e);
            throw new RuntimeException("Failed to generate EMI schedule: " + e.getMessage());
        }
    }

    /**
     * Check if all EMIs are paid for a loan
     */
    public boolean areAllEmisPaid(Long loanId) {
        logger.info("Checking if all EMIs are paid for loan ID: {}", loanId);

        String url = emiServiceUrl + "/api/internal/emis/loan/" + loanId + "/all-paid";

        try {
            Boolean result = restTemplate.getForObject(url, Boolean.class);
            logger.info("All EMIs paid for loan {}: {}", loanId, result);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            logger.error("Error checking EMI payment status for loan ID: {}", loanId, e);
            return false;
        }
    }
}
