package com.loanmanagement.notification.domain.service;

/**
 * Service interface for sending emails
 */
public interface EmailService {

    /**
     * Send a simple email
     *
     * @param to      recipient email address
     * @param subject email subject
     * @param body    email body (can be HTML)
     */
    void sendEmail(String to, String subject, String body);

    /**
     * Send email asynchronously
     *
     * @param to      recipient email address
     * @param subject email subject
     * @param body    email body (can be HTML)
     */
    void sendEmailAsync(String to, String subject, String body);

    /**
     * Check if email service is configured and available
     *
     * @return true if email can be sent, false otherwise
     */
    boolean isEmailConfigured();
}
