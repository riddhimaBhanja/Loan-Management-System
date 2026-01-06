package com.loanmanagement.emi.domain.model;

/**
 * Enum representing payment methods for EMI payments
 */
public enum PaymentMethod {
    CASH("Cash"),
    CHEQUE("Cheque"),
    NEFT("NEFT"),
    RTGS("RTGS"),
    UPI("UPI"),
    DEBIT_CARD("Debit Card"),
    CREDIT_CARD("Credit Card"),
    NET_BANKING("Net Banking"),
    DEMAND_DRAFT("Demand Draft");

    private final String displayName;

    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Check if payment method requires transaction reference
     */
    public boolean requiresTransactionReference() {
        return this != CASH;
    }
}
