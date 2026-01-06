package com.loanmanagement.notification.domain.model;

/**
 * Enum representing the status of a notification
 */
public enum NotificationStatus {
    PENDING("Pending - Not yet sent"),
    SENT("Successfully sent"),
    FAILED("Failed to send");

    private final String description;

    NotificationStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
