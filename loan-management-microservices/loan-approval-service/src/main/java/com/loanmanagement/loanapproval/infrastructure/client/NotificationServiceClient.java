package com.loanmanagement.loanapproval.infrastructure.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Client for communicating with Notification Service
 */
@Component
public class NotificationServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceClient.class);

    private final RestTemplate restTemplate;
    private final String notificationServiceUrl;

    public NotificationServiceClient(
            RestTemplate restTemplate,
            @Value("${services.notification-service.url}") String notificationServiceUrl) {
        this.restTemplate = restTemplate;
        this.notificationServiceUrl = notificationServiceUrl;
    }

    /**
     * Send loan approved notification
     */
    public void sendLoanApprovedNotification(String email, String name, String loanId,
                                              String amount, String approvedAmount) {
        logger.info("Sending loan approved notification to: {}", email);

        String url = UriComponentsBuilder.fromHttpUrl(notificationServiceUrl)
                .path("/api/internal/notifications/loan-approved")
                .queryParam("email", email)
                .queryParam("name", name)
                .queryParam("loanId", loanId)
                .queryParam("amount", amount)
                .queryParam("approvedAmount", approvedAmount)
                .toUriString();

        try {
            restTemplate.postForEntity(url, null, Void.class);
            logger.info("Loan approved notification sent successfully to: {}", email);
        } catch (Exception e) {
            logger.error("Failed to send loan approved notification to: {}. Error: {}", email, e.getMessage());
        }
    }

    /**
     * Send loan rejected notification
     */
    public void sendLoanRejectedNotification(String email, String name, String loanId, String reason) {
        logger.info("Sending loan rejected notification to: {}", email);

        String url = UriComponentsBuilder.fromHttpUrl(notificationServiceUrl)
                .path("/api/internal/notifications/loan-rejected")
                .queryParam("email", email)
                .queryParam("name", name)
                .queryParam("loanId", loanId)
                .queryParam("reason", reason != null ? reason : "")
                .toUriString();

        try {
            restTemplate.postForEntity(url, null, Void.class);
            logger.info("Loan rejected notification sent successfully to: {}", email);
        } catch (Exception e) {
            logger.error("Failed to send loan rejected notification to: {}. Error: {}", email, e.getMessage());
        }
    }

    /**
     * Send loan closed notification
     */
    public void sendLoanClosedNotification(String email, String name, String loanId) {
        logger.info("Sending loan closed notification to: {}", email);

        String url = UriComponentsBuilder.fromHttpUrl(notificationServiceUrl)
                .path("/api/internal/notifications/loan-closed")
                .queryParam("email", email)
                .queryParam("name", name)
                .queryParam("loanId", loanId)
                .toUriString();

        try {
            restTemplate.postForEntity(url, null, Void.class);
            logger.info("Loan closed notification sent successfully to: {}", email);
        } catch (Exception e) {
            logger.error("Failed to send loan closed notification to: {}. Error: {}", email, e.getMessage());
        }
    }
}
