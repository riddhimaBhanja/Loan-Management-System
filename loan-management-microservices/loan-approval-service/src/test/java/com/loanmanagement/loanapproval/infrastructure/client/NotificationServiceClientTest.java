package com.loanmanagement.loanapproval.infrastructure.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private NotificationServiceClient notificationServiceClient;

    private final String notificationServiceUrl = "http://notification-service";

    @BeforeEach
    void setUp() {
        notificationServiceClient =
                new NotificationServiceClient(restTemplate, notificationServiceUrl);
    }

    @Test
    void sendLoanApprovedNotification_success() {
        when(restTemplate.postForEntity(anyString(), eq(null), eq(Void.class)))
                .thenReturn(ResponseEntity.ok().build());

        assertDoesNotThrow(() ->
                notificationServiceClient.sendLoanApprovedNotification(
                        "test@example.com",
                        "John",
                        "1",
                        "100000",
                        "90000"
                )
        );
    }

    @Test
    void sendLoanApprovedNotification_exception_handled() {
        doThrow(new RuntimeException("error"))
                .when(restTemplate)
                .postForEntity(anyString(), eq(null), eq(Void.class));

        assertDoesNotThrow(() ->
                notificationServiceClient.sendLoanApprovedNotification(
                        "test@example.com",
                        "John",
                        "1",
                        "100000",
                        "90000"
                )
        );
    }

    @Test
    void sendLoanRejectedNotification_success() {
        when(restTemplate.postForEntity(anyString(), eq(null), eq(Void.class)))
                .thenReturn(ResponseEntity.ok().build());

        assertDoesNotThrow(() ->
                notificationServiceClient.sendLoanRejectedNotification(
                        "test@example.com",
                        "John",
                        "2",
                        "Invalid documents"
                )
        );
    }

    @Test
    void sendLoanRejectedNotification_nullReason() {
        when(restTemplate.postForEntity(anyString(), eq(null), eq(Void.class)))
                .thenReturn(ResponseEntity.ok().build());

        assertDoesNotThrow(() ->
                notificationServiceClient.sendLoanRejectedNotification(
                        "test@example.com",
                        "John",
                        "2",
                        null
                )
        );
    }

    @Test
    void sendLoanRejectedNotification_exception_handled() {
        doThrow(new RuntimeException("error"))
                .when(restTemplate)
                .postForEntity(anyString(), eq(null), eq(Void.class));

        assertDoesNotThrow(() ->
                notificationServiceClient.sendLoanRejectedNotification(
                        "test@example.com",
                        "John",
                        "2",
                        "Rejected"
                )
        );
    }

    @Test
    void sendLoanClosedNotification_success() {
        when(restTemplate.postForEntity(anyString(), eq(null), eq(Void.class)))
                .thenReturn(ResponseEntity.ok().build());

        assertDoesNotThrow(() ->
                notificationServiceClient.sendLoanClosedNotification(
                        "test@example.com",
                        "John",
                        "3"
                )
        );
    }

    @Test
    void sendLoanClosedNotification_exception_handled() {
        doThrow(new RuntimeException("error"))
                .when(restTemplate)
                .postForEntity(anyString(), eq(null), eq(Void.class));

        assertDoesNotThrow(() ->
                notificationServiceClient.sendLoanClosedNotification(
                        "test@example.com",
                        "John",
                        "3"
                )
        );
    }
}
