package com.loanmanagement.notification.domain.service;

import com.loanmanagement.notification.application.dto.request.CreateTemplateRequest;
import com.loanmanagement.notification.application.dto.request.SendNotificationRequest;
import com.loanmanagement.notification.domain.model.Notification;
import com.loanmanagement.notification.domain.model.NotificationStatus;
import com.loanmanagement.notification.domain.model.NotificationTemplate;
import com.loanmanagement.notification.domain.model.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * Service interface for notification management
 */
public interface NotificationService {

    /**
     * Send a notification
     */
    Notification sendNotification(SendNotificationRequest request);

    /**
     * Send a notification using a template
     */
    Notification sendNotificationFromTemplate(
            NotificationType type,
            String recipientEmail,
            String recipientName,
            Map<String, String> placeholders
    );

    /**
     * Get notification by ID
     */
    Notification getNotificationById(Long id);

    /**
     * Get all notifications with pagination
     */
    Page<Notification> getAllNotifications(Pageable pageable);

    /**
     * Get notifications by recipient email
     */
    Page<Notification> getNotificationsByRecipient(String recipientEmail, Pageable pageable);

    /**
     * Get notifications by type
     */
    Page<Notification> getNotificationsByType(NotificationType type, Pageable pageable);

    /**
     * Get notifications by status
     */
    Page<Notification> getNotificationsByStatus(NotificationStatus status, Pageable pageable);

    /**
     * Get notifications by user ID
     */
    Page<Notification> getNotificationsByUserId(Long userId, Pageable pageable);

    /**
     * Get notifications by loan application ID
     */
    List<Notification> getNotificationsByLoanApplicationId(Long loanApplicationId);

    /**
     * Retry failed notifications
     */
    void retryFailedNotifications();

    /**
     * Create a notification template
     */
    NotificationTemplate createTemplate(CreateTemplateRequest request);

    /**
     * Update a notification template
     */
    NotificationTemplate updateTemplate(Long id, CreateTemplateRequest request);

    /**
     * Get template by ID
     */
    NotificationTemplate getTemplateById(Long id);

    /**
     * Get all templates
     */
    List<NotificationTemplate> getAllTemplates();

    /**
     * Get template by type
     */
    NotificationTemplate getTemplateByType(NotificationType type);

    /**
     * Delete template
     */
    void deleteTemplate(Long id);

    /**
     * Get notification statistics
     */
    Map<String, Object> getNotificationStats();
}
