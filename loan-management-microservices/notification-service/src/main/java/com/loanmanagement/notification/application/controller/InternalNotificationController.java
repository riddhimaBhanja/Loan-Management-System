package com.loanmanagement.notification.application.controller;

import com.loanmanagement.notification.application.dto.request.SendNotificationRequest;
import com.loanmanagement.notification.application.dto.response.NotificationResponse;
import com.loanmanagement.notification.domain.model.Notification;
import com.loanmanagement.notification.domain.model.NotificationType;
import com.loanmanagement.notification.domain.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Internal REST Controller for inter-service communication
 * These endpoints are called by other microservices and are not JWT-protected
 */
@RestController
@RequestMapping("/api/internal/notifications")
@RequiredArgsConstructor
@Slf4j
public class InternalNotificationController {

    private final NotificationService notificationService;

    /**
     * Send a notification (called by other services)
     * POST /api/internal/notifications/send
     */
    @PostMapping("/send")
    public ResponseEntity<NotificationResponse> sendNotification(
            @Valid @RequestBody SendNotificationRequest request) {

        log.info("Internal API: Sending notification to: {}", request.getRecipientEmail());

        Notification notification = notificationService.sendNotification(request);
        NotificationResponse response = mapToResponse(notification);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Send notification using template
     * POST /api/internal/notifications/send-template
     */
    @PostMapping("/send-template")
    public ResponseEntity<NotificationResponse> sendNotificationFromTemplate(
            @RequestParam NotificationType type,
            @RequestParam String recipientEmail,
            @RequestParam(required = false) String recipientName,
            @RequestBody Map<String, String> placeholders) {

        log.info("Internal API: Sending notification from template. Type: {}, Recipient: {}",
                type, recipientEmail);

        Notification notification = notificationService.sendNotificationFromTemplate(
                type, recipientEmail, recipientName, placeholders);
        NotificationResponse response = mapToResponse(notification);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Send loan submitted notification
     * POST /api/internal/notifications/loan-submitted
     */
    @PostMapping("/loan-submitted")
    public ResponseEntity<NotificationResponse> sendLoanSubmittedNotification(
            @RequestParam String email,
            @RequestParam String name,
            @RequestParam String loanId,
            @RequestParam String amount) {

        log.info("Internal API: Sending loan submitted notification to: {}", email);

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("recipientName", name);
        placeholders.put("loanId", loanId);
        placeholders.put("loanAmount", amount);

        Notification notification = notificationService.sendNotificationFromTemplate(
                NotificationType.LOAN_SUBMITTED, email, name, placeholders);
        NotificationResponse response = mapToResponse(notification);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Send loan approved notification
     * POST /api/internal/notifications/loan-approved
     */
    @PostMapping("/loan-approved")
    public ResponseEntity<NotificationResponse> sendLoanApprovedNotification(
            @RequestParam String email,
            @RequestParam String name,
            @RequestParam String loanId,
            @RequestParam String amount,
            @RequestParam String approvedAmount) {

        log.info("Internal API: Sending loan approved notification to: {}", email);

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("recipientName", name);
        placeholders.put("loanId", loanId);
        placeholders.put("loanAmount", amount);
        placeholders.put("approvedAmount", approvedAmount);

        Notification notification = notificationService.sendNotificationFromTemplate(
                NotificationType.LOAN_APPROVED, email, name, placeholders);
        NotificationResponse response = mapToResponse(notification);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Send loan rejected notification
     * POST /api/internal/notifications/loan-rejected
     */
    @PostMapping("/loan-rejected")
    public ResponseEntity<NotificationResponse> sendLoanRejectedNotification(
            @RequestParam String email,
            @RequestParam String name,
            @RequestParam String loanId,
            @RequestParam(required = false) String reason) {

        log.info("Internal API: Sending loan rejected notification to: {}", email);

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("recipientName", name);
        placeholders.put("loanId", loanId);
        placeholders.put("reason", reason != null ? reason : "Please contact us for more details");

        Notification notification = notificationService.sendNotificationFromTemplate(
                NotificationType.LOAN_REJECTED, email, name, placeholders);
        NotificationResponse response = mapToResponse(notification);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Send loan disbursed notification
     * POST /api/internal/notifications/loan-disbursed
     */
    @PostMapping("/loan-disbursed")
    public ResponseEntity<NotificationResponse> sendLoanDisbursedNotification(
            @RequestParam String email,
            @RequestParam String name,
            @RequestParam String loanId,
            @RequestParam String amount,
            @RequestParam String disbursementDate) {

        log.info("Internal API: Sending loan disbursed notification to: {}", email);

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("recipientName", name);
        placeholders.put("loanId", loanId);
        placeholders.put("amount", amount);
        placeholders.put("disbursementDate", disbursementDate);

        Notification notification = notificationService.sendNotificationFromTemplate(
                NotificationType.LOAN_DISBURSED, email, name, placeholders);
        NotificationResponse response = mapToResponse(notification);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Send EMI due notification
     * POST /api/internal/notifications/emi-due
     */
    @PostMapping("/emi-due")
    public ResponseEntity<NotificationResponse> sendEmiDueNotification(
            @RequestParam String email,
            @RequestParam String name,
            @RequestParam String emiNumber,
            @RequestParam String amount,
            @RequestParam String dueDate) {

        log.info("Internal API: Sending EMI due notification to: {}", email);

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("recipientName", name);
        placeholders.put("emiNumber", emiNumber);
        placeholders.put("amount", amount);
        placeholders.put("dueDate", dueDate);

        Notification notification = notificationService.sendNotificationFromTemplate(
                NotificationType.EMI_DUE, email, name, placeholders);
        NotificationResponse response = mapToResponse(notification);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Send EMI paid notification
     * POST /api/internal/notifications/emi-paid
     */
    @PostMapping("/emi-paid")
    public ResponseEntity<NotificationResponse> sendEmiPaidNotification(
            @RequestParam String email,
            @RequestParam String name,
            @RequestParam String emiNumber,
            @RequestParam String amount,
            @RequestParam String paymentDate) {

        log.info("Internal API: Sending EMI paid notification to: {}", email);

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("recipientName", name);
        placeholders.put("emiNumber", emiNumber);
        placeholders.put("amount", amount);
        placeholders.put("paymentDate", paymentDate);

        Notification notification = notificationService.sendNotificationFromTemplate(
                NotificationType.EMI_PAID, email, name, placeholders);
        NotificationResponse response = mapToResponse(notification);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Send account created notification
     * POST /api/internal/notifications/account-created
     */
    @PostMapping("/account-created")
    public ResponseEntity<NotificationResponse> sendAccountCreatedNotification(
            @RequestParam String email,
            @RequestParam String name,
            @RequestParam String username) {

        log.info("Internal API: Sending account created notification to: {}", email);

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("recipientName", name);
        placeholders.put("username", username);
        placeholders.put("email", email);

        Notification notification = notificationService.sendNotificationFromTemplate(
                NotificationType.ACCOUNT_CREATED, email, name, placeholders);
        NotificationResponse response = mapToResponse(notification);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Send loan closed notification
     * POST /api/internal/notifications/loan-closed
     */
    @PostMapping("/loan-closed")
    public ResponseEntity<NotificationResponse> sendLoanClosedNotification(
            @RequestParam String email,
            @RequestParam String name,
            @RequestParam String loanId) {

        log.info("Internal API: Sending loan closed notification to: {}", email);

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("recipientName", name);
        placeholders.put("loanId", loanId);

        Notification notification = notificationService.sendNotificationFromTemplate(
                NotificationType.LOAN_CLOSED, email, name, placeholders);
        NotificationResponse response = mapToResponse(notification);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Health check endpoint
     * GET /api/internal/notifications/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "notification-service");
        return ResponseEntity.ok(response);
    }

    // ========== Helper Methods ==========

    private NotificationResponse mapToResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .recipientEmail(notification.getRecipientEmail())
                .recipientName(notification.getRecipientName())
                .subject(notification.getSubject())
                .body(notification.getBody())
                .type(notification.getType())
                .status(notification.getStatus())
                .sentAt(notification.getSentAt())
                .errorMessage(notification.getErrorMessage())
                .retryCount(notification.getRetryCount())
                .userId(notification.getUserId())
                .loanApplicationId(notification.getLoanApplicationId())
                .emiId(notification.getEmiId())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
