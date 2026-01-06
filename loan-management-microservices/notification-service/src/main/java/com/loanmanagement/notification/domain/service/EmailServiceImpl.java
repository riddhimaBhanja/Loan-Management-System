package com.loanmanagement.notification.domain.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Implementation of EmailService
 * Handles email sending with Spring JavaMailSender
 */
@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.default-from:noreply@loanmanagement.com}")
    private String defaultFrom;

    @Value("${spring.mail.username:}")
    private String mailUsername;

    @Override
    public void sendEmail(String to, String subject, String body) {
        if (!isEmailConfigured()) {
            log.warn("Email service is not configured. Cannot send email to: {}", to);
            log.info("Email would have been sent - To: {}, Subject: {}", to, subject);
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(defaultFrom);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true); // true = HTML content

            mailSender.send(message);
            log.info("Email sent successfully to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send email to: {}. Error: {}", to, e.getMessage(), e);
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }

    @Override
    @Async
    public void sendEmailAsync(String to, String subject, String body) {
        log.debug("Sending email asynchronously to: {}", to);
        sendEmail(to, subject, body);
    }

    @Override
    public boolean isEmailConfigured() {
        // Check if JavaMailSender bean exists and username is configured
        boolean configured = mailSender != null &&
                            mailUsername != null &&
                            !mailUsername.trim().isEmpty();

        if (!configured) {
            log.debug("Email service not configured. JavaMailSender exists: {}, Username configured: {}",
                     mailSender != null, mailUsername != null && !mailUsername.trim().isEmpty());
        }

        return configured;
    }
}
