package com.loanmanagement.emi.shared.constants;

/**
 * Constants for error and success messages
 */
public final class MessageConstants {

    private MessageConstants() {
        // Private constructor to prevent instantiation
    }

    // EMI Schedule Messages
    public static final String EMI_NOT_FOUND = "EMI schedule not found";
    public static final String EMI_ALREADY_PAID = "EMI has already been paid";
    public static final String EMI_SCHEDULE_GENERATED = "EMI schedule generated successfully";
    public static final String EMI_SCHEDULE_NOT_FOUND = "EMI schedule not found for the loan";
    public static final String EMI_ALREADY_EXISTS = "EMI schedule already exists for this loan";

    // Payment Messages
    public static final String PAYMENT_RECORDED = "Payment recorded successfully";
    public static final String PAYMENT_NOT_FOUND = "Payment record not found";
    public static final String INSUFFICIENT_PAYMENT = "Payment amount is less than EMI amount";
    public static final String INVALID_PAYMENT_AMOUNT = "Invalid payment amount";
    public static final String TRANSACTION_REFERENCE_REQUIRED = "Transaction reference is required for this payment method";
    public static final String DUPLICATE_TRANSACTION_REFERENCE = "Transaction reference already exists";

    // Loan Messages
    public static final String LOAN_NOT_FOUND = "Loan not found";
    public static final String LOAN_NOT_DISBURSED = "Loan has not been disbursed yet";
    public static final String LOAN_ALREADY_CLOSED = "Loan is already closed";

    // User Messages
    public static final String USER_NOT_FOUND = "User not found";
    public static final String UNAUTHORIZED_ACCESS = "You are not authorized to perform this operation";
    public static final String ACCESS_DENIED = "Access denied. You can only view your own loan information";

    // Validation Messages
    public static final String INVALID_LOAN_ID = "Invalid loan ID";
    public static final String INVALID_CUSTOMER_ID = "Invalid customer ID";
    public static final String INVALID_EMI_SCHEDULE_ID = "Invalid EMI schedule ID";
    public static final String INVALID_DATE_RANGE = "Invalid date range";

    // Success Messages
    public static final String OVERDUE_EMIS_UPDATED = "Overdue EMIs updated successfully";
    public static final String ALL_EMIS_PAID = "All EMIs have been paid";
}
