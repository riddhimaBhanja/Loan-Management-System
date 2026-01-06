package com.loanmanagement.loanapp.domain.enums;

/**
 * Enum representing employment status of a loan applicant
 */
public enum EmploymentStatus {
    SALARIED("Salaried"),
    SELF_EMPLOYED("Self Employed"),
    BUSINESS_OWNER("Business Owner"),
    UNEMPLOYED("Unemployed"),
    RETIRED("Retired");

    private final String displayName;

    EmploymentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
