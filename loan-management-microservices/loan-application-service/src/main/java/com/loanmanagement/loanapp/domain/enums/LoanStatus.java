package com.loanmanagement.loanapp.domain.enums;

/**
 * Enum representing the lifecycle status of a loan
 */
public enum LoanStatus {
    PENDING("Pending"),
    APPROVED("Approved"),
    REJECTED("Rejected"),
    DISBURSED("Disbursed"),
    CLOSED("Closed");

    private final String displayName;

    LoanStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
