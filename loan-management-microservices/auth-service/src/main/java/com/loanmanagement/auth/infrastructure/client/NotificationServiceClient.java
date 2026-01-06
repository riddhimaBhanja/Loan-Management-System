package com.loanmanagement.auth.infrastructure.client;

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
     * Send account created notification
     */
    public void sendAccountCreatedNotification(String email, String name, String username) {
        logger.info("Sending account created notification to: {}", email);

        String url = UriComponentsBuilder.fromHttpUrl(notificationServiceUrl)
                .path("/api/internal/notifications/account-created")
                .queryParam("email", email)
                .queryParam("name", name)
                .queryParam("username", username)
                .toUriString();

        try {
            restTemplate.postForEntity(url, null, Void.class);
            logger.info("Account created notification sent successfully to: {}", email);
        } catch (Exception e) {
            logger.error("Failed to send account created notification to: {}. Error: {}", email, e.getMessage());
            // Don't throw exception - notification failure shouldn't break registration
        }
    }
}
