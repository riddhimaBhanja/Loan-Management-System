package com.loanmanagement.emi.domain.model;

/**
 * Enum representing the payment status of an EMI installment
 */
public enum EmiStatus {
    PENDING("Pending"),
    PAID("Paid"),
    OVERDUE("Overdue"),
    PARTIAL_PAID("Partial Paid");

    private final String displayName;

    EmiStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Check if EMI can be marked as paid
     */
    public boolean canMarkAsPaid() {
        return this == PENDING || this == OVERDUE || this == PARTIAL_PAID;
    }

    /**
     * Check if EMI is unpaid
     */
    public boolean isUnpaid() {
        return this == PENDING || this == OVERDUE || this == PARTIAL_PAID;
    }
}
