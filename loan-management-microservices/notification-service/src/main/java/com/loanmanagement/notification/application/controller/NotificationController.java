package com.loanmanagement.notification.application.controller;

import com.loanmanagement.notification.application.dto.request.CreateTemplateRequest;
import com.loanmanagement.notification.application.dto.request.SendNotificationRequest;
import com.loanmanagement.notification.application.dto.response.NotificationResponse;
import com.loanmanagement.notification.application.dto.response.TemplateResponse;
import com.loanmanagement.notification.domain.model.Notification;
import com.loanmanagement.notification.domain.model.NotificationStatus;
import com.loanmanagement.notification.domain.model.NotificationTemplate;
import com.loanmanagement.notification.domain.model.NotificationType;
import com.loanmanagement.notification.domain.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST Controller for notification management
 * Secured endpoints for authenticated users
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * Send a notification
     * POST /api/notifications
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LOAN_OFFICER')")
    public ResponseEntity<NotificationResponse> sendNotification(
            @Valid @RequestBody SendNotificationRequest request) {

        log.info("REST request to send notification to: {}", request.getRecipientEmail());

        Notification notification = notificationService.sendNotification(request);
        NotificationResponse response = mapToResponse(notification);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get notification by ID
     * GET /api/notifications/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LOAN_OFFICER')")
    public ResponseEntity<NotificationResponse> getNotificationById(@PathVariable Long id) {
        log.info("REST request to get notification by ID: {}", id);

        Notification notification = notificationService.getNotificationById(id);
        NotificationResponse response = mapToResponse(notification);

        return ResponseEntity.ok(response);
    }

    /**
     * Get all notifications with pagination
     * GET /api/notifications?page=0&size=10
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LOAN_OFFICER')")
    public ResponseEntity<Page<NotificationResponse>> getAllNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("REST request to get all notifications - page: {}, size: {}", page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<Notification> notifications = notificationService.getAllNotifications(pageable);
        Page<NotificationResponse> response = notifications.map(this::mapToResponse);

        return ResponseEntity.ok(response);
    }

    /**
     * Get notifications by recipient email
     * GET /api/notifications/recipient/{email}
     */
    @GetMapping("/recipient/{email}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LOAN_OFFICER')")
    public ResponseEntity<Page<NotificationResponse>> getNotificationsByRecipient(
            @PathVariable String email,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("REST request to get notifications by recipient: {}", email);

        Pageable pageable = PageRequest.of(page, size);
        Page<Notification> notifications = notificationService.getNotificationsByRecipient(email, pageable);
        Page<NotificationResponse> response = notifications.map(this::mapToResponse);

        return ResponseEntity.ok(response);
    }

    /**
     * Get notifications by type
     * GET /api/notifications/type/{type}
     */
    @GetMapping("/type/{type}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LOAN_OFFICER')")
    public ResponseEntity<Page<NotificationResponse>> getNotificationsByType(
            @PathVariable NotificationType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("REST request to get notifications by type: {}", type);

        Pageable pageable = PageRequest.of(page, size);
        Page<Notification> notifications = notificationService.getNotificationsByType(type, pageable);
        Page<NotificationResponse> response = notifications.map(this::mapToResponse);

        return ResponseEntity.ok(response);
    }

    /**
     * Get notifications by status
     * GET /api/notifications/status/{status}
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LOAN_OFFICER')")
    public ResponseEntity<Page<NotificationResponse>> getNotificationsByStatus(
            @PathVariable NotificationStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("REST request to get notifications by status: {}", status);

        Pageable pageable = PageRequest.of(page, size);
        Page<Notification> notifications = notificationService.getNotificationsByStatus(status, pageable);
        Page<NotificationResponse> response = notifications.map(this::mapToResponse);

        return ResponseEntity.ok(response);
    }

    /**
     * Get notifications by user ID
     * GET /api/notifications/user/{userId}
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LOAN_OFFICER')")
    public ResponseEntity<Page<NotificationResponse>> getNotificationsByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("REST request to get notifications by user ID: {}", userId);

        Pageable pageable = PageRequest.of(page, size);
        Page<Notification> notifications = notificationService.getNotificationsByUserId(userId, pageable);
        Page<NotificationResponse> response = notifications.map(this::mapToResponse);

        return ResponseEntity.ok(response);
    }

    /**
     * Get notifications by loan application ID
     * GET /api/notifications/loan/{loanApplicationId}
     */
    @GetMapping("/loan/{loanApplicationId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LOAN_OFFICER')")
    public ResponseEntity<List<NotificationResponse>> getNotificationsByLoanApplicationId(
            @PathVariable Long loanApplicationId) {

        log.info("REST request to get notifications by loan application ID: {}", loanApplicationId);

        List<Notification> notifications = notificationService.getNotificationsByLoanApplicationId(loanApplicationId);
        List<NotificationResponse> response = notifications.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    /**
     * Retry failed notifications
     * POST /api/notifications/retry
     */
    @PostMapping("/retry")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> retryFailedNotifications() {
        log.info("REST request to retry failed notifications");

        notificationService.retryFailedNotifications();

        Map<String, String> response = new HashMap<>();
        response.put("message", "Retry process initiated for failed notifications");

        return ResponseEntity.ok(response);
    }

    /**
     * Get notification statistics
     * GET /api/notifications/stats
     */
    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'LOAN_OFFICER')")
    public ResponseEntity<Map<String, Object>> getNotificationStats() {
        log.info("REST request to get notification statistics");

        Map<String, Object> stats = notificationService.getNotificationStats();
        return ResponseEntity.ok(stats);
    }

    // ========== Template Management Endpoints ==========

    /**
     * Create a notification template
     * POST /api/notifications/templates
     */
    @PostMapping("/templates")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TemplateResponse> createTemplate(
            @Valid @RequestBody CreateTemplateRequest request) {

        log.info("REST request to create notification template: {}", request.getName());

        NotificationTemplate template = notificationService.createTemplate(request);
        TemplateResponse response = mapToTemplateResponse(template);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Update a notification template
     * PUT /api/notifications/templates/{id}
     */
    @PutMapping("/templates/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TemplateResponse> updateTemplate(
            @PathVariable Long id,
            @Valid @RequestBody CreateTemplateRequest request) {

        log.info("REST request to update notification template ID: {}", id);

        NotificationTemplate template = notificationService.updateTemplate(id, request);
        TemplateResponse response = mapToTemplateResponse(template);

        return ResponseEntity.ok(response);
    }

    /**
     * Get template by ID
     * GET /api/notifications/templates/{id}
     */
    @GetMapping("/templates/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LOAN_OFFICER')")
    public ResponseEntity<TemplateResponse> getTemplateById(@PathVariable Long id) {
        log.info("REST request to get template by ID: {}", id);

        NotificationTemplate template = notificationService.getTemplateById(id);
        TemplateResponse response = mapToTemplateResponse(template);

        return ResponseEntity.ok(response);
    }

    /**
     * Get all templates
     * GET /api/notifications/templates
     */
    @GetMapping("/templates")
    @PreAuthorize("hasAnyRole('ADMIN', 'LOAN_OFFICER')")
    public ResponseEntity<List<TemplateResponse>> getAllTemplates() {
        log.info("REST request to get all templates");

        List<NotificationTemplate> templates = notificationService.getAllTemplates();
        List<TemplateResponse> response = templates.stream()
                .map(this::mapToTemplateResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    /**
     * Delete a template
     * DELETE /api/notifications/templates/{id}
     */
    @DeleteMapping("/templates/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteTemplate(@PathVariable Long id) {
        log.info("REST request to delete template ID: {}", id);

        notificationService.deleteTemplate(id);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Template deleted successfully");

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

    private TemplateResponse mapToTemplateResponse(NotificationTemplate template) {
        return TemplateResponse.builder()
                .id(template.getId())
                .name(template.getName())
                .type(template.getType())
                .subject(template.getSubject())
                .bodyTemplate(template.getBodyTemplate())
                .isActive(template.getIsActive())
                .description(template.getDescription())
                .createdAt(template.getCreatedAt())
                .updatedAt(template.getUpdatedAt())
                .build();
    }
}
