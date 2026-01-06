package com.loanmanagement.auth.domain.model;

/**
 * Enum representing user roles in the system
 */
public enum RoleType {
    ADMIN("Admin"),
    LOAN_OFFICER("Loan Officer"),
    CUSTOMER("Customer");

    private final String displayName;

    RoleType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getRoleName() {
        return "ROLE_" + this.name();
    }
}
