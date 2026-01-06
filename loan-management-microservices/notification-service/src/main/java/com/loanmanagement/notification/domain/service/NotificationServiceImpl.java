package com.loanmanagement.notification.domain.service;

import com.loanmanagement.common.exception.ResourceNotFoundException;
import com.loanmanagement.notification.application.dto.request.CreateTemplateRequest;
import com.loanmanagement.notification.application.dto.request.SendNotificationRequest;
import com.loanmanagement.notification.domain.model.Notification;
import com.loanmanagement.notification.domain.model.NotificationStatus;
import com.loanmanagement.notification.domain.model.NotificationTemplate;
import com.loanmanagement.notification.domain.model.NotificationType;
import com.loanmanagement.notification.domain.repository.NotificationRepository;
import com.loanmanagement.notification.domain.repository.NotificationTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of NotificationService
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationTemplateRepository templateRepository;
    private final EmailService emailService;

    @Override
    @Transactional
    public Notification sendNotification(SendNotificationRequest request) {
        log.info("Creating notification for recipient: {}", request.getRecipientEmail());

        // Create notification record
        Notification notification = Notification.builder()
                .recipientEmail(request.getRecipientEmail())
                .recipientName(request.getRecipientName())
                .subject(request.getSubject())
                .body(request.getBody())
                .type(request.getType())
                .status(NotificationStatus.PENDING)
                .userId(request.getUserId())
                .loanApplicationId(request.getLoanApplicationId())
                .emiId(request.getEmiId())
                .build();

        notification = notificationRepository.save(notification);

        // Send email asynchronously
        sendEmailAsync(notification);

        return notification;
    }

    @Override
    @Transactional
    public Notification sendNotificationFromTemplate(
            NotificationType type,
            String recipientEmail,
            String recipientName,
            Map<String, String> placeholders) {

        log.info("Sending notification from template. Type: {}, Recipient: {}", type, recipientEmail);

        // Get template
        NotificationTemplate template = templateRepository
                .findFirstByTypeAndIsActiveTrue(type)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No active template found for type: " + type));

        // Replace placeholders
        String subject = template.replaceSubjectPlaceholders(placeholders);
        String body = template.replacePlaceholders(placeholders);

        // Create and send notification
        SendNotificationRequest request = SendNotificationRequest.builder()
                .recipientEmail(recipientEmail)
                .recipientName(recipientName)
                .subject(subject)
                .body(body)
                .type(type)
                .build();

        return sendNotification(request);
    }

    @Async
    protected void sendEmailAsync(Notification notification) {
        try {
            log.debug("Attempting to send email for notification ID: {}", notification.getId());

            emailService.sendEmailAsync(
                    notification.getRecipientEmail(),
                    notification.getSubject(),
                    notification.getBody()
            );

            // Mark as sent
            notification.markAsSent();
            notificationRepository.save(notification);

            log.info("Notification sent successfully. ID: {}", notification.getId());

        } catch (Exception e) {
            log.error("Failed to send notification ID: {}. Error: {}",
                    notification.getId(), e.getMessage(), e);

            // Mark as failed
            notification.markAsFailed(e.getMessage());
            notificationRepository.save(notification);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Notification getNotificationById(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Notification> getAllNotifications(Pageable pageable) {
        return notificationRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Notification> getNotificationsByRecipient(String recipientEmail, Pageable pageable) {
        return notificationRepository.findByRecipientEmailOrderByCreatedAtDesc(recipientEmail, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Notification> getNotificationsByType(NotificationType type, Pageable pageable) {
        return notificationRepository.findByTypeOrderByCreatedAtDesc(type, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Notification> getNotificationsByStatus(NotificationStatus status, Pageable pageable) {
        return notificationRepository.findByStatusOrderByCreatedAtDesc(status, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Notification> getNotificationsByUserId(Long userId, Pageable pageable) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notification> getNotificationsByLoanApplicationId(Long loanApplicationId) {
        return notificationRepository.findByLoanApplicationIdOrderByCreatedAtDesc(loanApplicationId);
    }

    @Override
    @Transactional
    public void retryFailedNotifications() {
        log.info("Retrying failed notifications...");

        List<Notification> failedNotifications = notificationRepository
                .findPendingNotificationsForRetry(3); // Max 3 retries

        for (Notification notification : failedNotifications) {
            log.debug("Retrying notification ID: {}", notification.getId());
            sendEmailAsync(notification);
        }

        log.info("Retry process completed. Processed {} notifications", failedNotifications.size());
    }

    @Override
    @Transactional
    public NotificationTemplate createTemplate(CreateTemplateRequest request) {
        log.info("Creating notification template: {}", request.getName());

        // Check if template with same name exists
        if (templateRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Template with name '" + request.getName() + "' already exists");
        }

        NotificationTemplate template = NotificationTemplate.builder()
                .name(request.getName())
                .type(request.getType())
                .subject(request.getSubject())
                .bodyTemplate(request.getBodyTemplate())
                .description(request.getDescription())
                .isActive(true)
                .build();

        template = templateRepository.save(template);
        log.info("Template created successfully. ID: {}", template.getId());

        return template;
    }

    @Override
    @Transactional
    public NotificationTemplate updateTemplate(Long id, CreateTemplateRequest request) {
        log.info("Updating notification template ID: {}", id);

        NotificationTemplate template = getTemplateById(id);

        // Check if name is being changed and if it conflicts
        if (!template.getName().equals(request.getName()) &&
                templateRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Template with name '" + request.getName() + "' already exists");
        }

        template.setName(request.getName());
        template.setType(request.getType());
        template.setSubject(request.getSubject());
        template.setBodyTemplate(request.getBodyTemplate());
        template.setDescription(request.getDescription());

        template = templateRepository.save(template);
        log.info("Template updated successfully. ID: {}", template.getId());

        return template;
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationTemplate getTemplateById(Long id) {
        return templateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Template not found with ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationTemplate> getAllTemplates() {
        return templateRepository.findAllByOrderByNameAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationTemplate getTemplateByType(NotificationType type) {
        return templateRepository.findFirstByTypeAndIsActiveTrue(type)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No active template found for type: " + type));
    }

    @Override
    @Transactional
    public void deleteTemplate(Long id) {
        log.info("Deleting notification template ID: {}", id);

        NotificationTemplate template = getTemplateById(id);
        templateRepository.delete(template);

        log.info("Template deleted successfully. ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getNotificationStats() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalNotifications", notificationRepository.count());
        stats.put("pendingNotifications", notificationRepository.countByStatus(NotificationStatus.PENDING));
        stats.put("sentNotifications", notificationRepository.countByStatus(NotificationStatus.SENT));
        stats.put("failedNotifications", notificationRepository.countByStatus(NotificationStatus.FAILED));

        // Recent notifications (last 7 days)
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        List<Notification> recentNotifications = notificationRepository.findRecentNotifications(sevenDaysAgo);
        stats.put("recentNotifications", recentNotifications.size());

        // Email service status
        stats.put("emailServiceConfigured", emailService.isEmailConfigured());

        return stats;
    }
}
