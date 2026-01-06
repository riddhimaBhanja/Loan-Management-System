package com.loanmanagement.auth.infrastructure.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationServiceClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private NotificationServiceClient notificationServiceClient;

    private final String notificationServiceUrl = "http://localhost:8085";

    @BeforeEach
    void setUp() {
        notificationServiceClient =
                new NotificationServiceClient(restTemplate, notificationServiceUrl);
    }

    @Test
    void sendAccountCreatedNotification_success() {
        String email = "test@example.com";
        String name = "Test User";
        String username = "testuser";

        notificationServiceClient.sendAccountCreatedNotification(email, name, username);

        verify(restTemplate).postForEntity(
                any(String.class),
                eq(null),
                eq(Void.class)
        );
    }

    @Test
    void sendAccountCreatedNotification_failure_shouldNotThrowException() {
        String email = "test@example.com";
        String name = "Test User";
        String username = "testuser";

        doThrow(new RuntimeException("Service unavailable"))
                .when(restTemplate)
                .postForEntity(any(String.class), eq(null), eq(Void.class));

        notificationServiceClient.sendAccountCreatedNotification(email, name, username);

        verify(restTemplate).postForEntity(
                any(String.class),
                eq(null),
                eq(Void.class)
        );
    }
}
