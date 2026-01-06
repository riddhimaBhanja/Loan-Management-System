package com.loanmanagement.notification.infrastructure.scheduler;

import com.loanmanagement.notification.domain.model.NotificationType;
import com.loanmanagement.notification.domain.service.NotificationService;
import com.loanmanagement.notification.infrastructure.client.AuthServiceClient;
import com.loanmanagement.notification.infrastructure.client.EmiServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Scheduled task to send EMI due reminders
 * Runs daily to check for upcoming EMI payments and send reminder emails
 */
@Component
@ConditionalOnProperty(name = "scheduler.emi-reminder.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
@Slf4j
public class EmiReminderScheduler {

    private final EmiServiceClient emiServiceClient;
    private final AuthServiceClient authServiceClient;
    private final NotificationService notificationService;

    @Value("${scheduler.emi-reminder.days-ahead:3}")
    private Integer daysAhead;

    /**
     * Send EMI due reminders
     * Scheduled to run daily at 9 AM (configurable via cron expression)
     */
    @Scheduled(cron = "${scheduler.emi-reminder.cron:0 0 9 * * ?}")
    public void sendEmiDueReminders() {
        log.info("Starting EMI due reminder job - checking EMIs due within next {} days", daysAhead);

        try {
            // Get upcoming EMIs from EMI service
            List<EmiServiceClient.EmiScheduleDto> upcomingEmis = emiServiceClient.getUpcomingEmis(daysAhead);

            if (upcomingEmis.isEmpty()) {
                log.info("No upcoming EMIs found. Reminder job completed.");
                return;
            }

            log.info("Found {} upcoming EMIs. Sending reminders...", upcomingEmis.size());

            int successCount = 0;
            int failureCount = 0;

            // Send reminder for each upcoming EMI
            for (EmiServiceClient.EmiScheduleDto emi : upcomingEmis) {
                try {
                    sendReminderForEmi(emi);
                    successCount++;
                } catch (Exception e) {
                    log.error("Failed to send reminder for EMI ID: {}. Error: {}", emi.getId(), e.getMessage());
                    failureCount++;
                }
            }

            log.info("EMI reminder job completed. Success: {}, Failures: {}", successCount, failureCount);

        } catch (Exception e) {
            log.error("EMI reminder job failed with error: {}", e.getMessage(), e);
        }
    }

    /**
     * Send reminder email for a specific EMI
     */
    private void sendReminderForEmi(EmiServiceClient.EmiScheduleDto emi) {
        log.debug("Processing EMI reminder - ID: {}, Loan: {}, Customer: {}, Due: {}",
                emi.getId(), emi.getLoanId(), emi.getCustomerId(), emi.getDueDate());

        // Get customer details from auth service
        AuthServiceClient.UserDetailsDto customer = authServiceClient.getUserById(emi.getCustomerId());

        if (customer == null) {
            log.warn("Customer not found for customer ID: {}. Skipping EMI reminder.", emi.getCustomerId());
            return;
        }

        // Prepare template placeholders
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("recipientName", customer.getFirstName() + " " + customer.getLastName());
        placeholders.put("emiNumber", emi.getEmiNumber().toString());
        placeholders.put("amount", emi.getEmiAmount().toString());
        placeholders.put("dueDate", emi.getDueDate().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")));

        // Send notification using template
        notificationService.sendNotificationFromTemplate(
                NotificationType.EMI_DUE,
                customer.getEmail(),
                customer.getFirstName() + " " + customer.getLastName(),
                placeholders
        );

        log.debug("EMI due reminder sent successfully to: {} for EMI ID: {}", customer.getEmail(), emi.getId());
    }
}
