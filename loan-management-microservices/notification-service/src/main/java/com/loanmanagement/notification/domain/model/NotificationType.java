package com.loanmanagement.notification.domain.model;

/**
 * Enum representing different types of notifications in the loan management system
 */
public enum NotificationType {
    LOAN_SUBMITTED("Loan Application Submitted"),
    LOAN_APPROVED("Loan Application Approved"),
    LOAN_REJECTED("Loan Application Rejected"),
    LOAN_DISBURSED("Loan Disbursement Completed"),
    EMI_DUE("EMI Payment Due"),
    EMI_PAID("EMI Payment Received"),
    LOAN_CLOSED("Loan Account Closed"),
    PAYMENT_REMINDER("Payment Reminder"),
    ACCOUNT_CREATED("Account Created"),
    PASSWORD_RESET("Password Reset Request");

    private final String description;

    NotificationType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
