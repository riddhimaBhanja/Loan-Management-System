package com.loanmanagement.notification.application.dto.response;

import com.loanmanagement.notification.domain.model.NotificationStatus;
import com.loanmanagement.notification.domain.model.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for notification response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {

    private Long id;
    private String recipientEmail;
    private String recipientName;
    private String subject;
    private String body;
    private NotificationType type;
    private NotificationStatus status;
    private LocalDateTime sentAt;
    private String errorMessage;
    private Integer retryCount;
    private Long userId;
    private Long loanApplicationId;
    private Long emiId;
    private LocalDateTime createdAt;
}
