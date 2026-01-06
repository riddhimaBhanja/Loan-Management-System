package com.loanmanagement.loanapproval.shared.constants;

/**
 * Constants for messages used throughout the Loan Approval Service
 */
public final class MessageConstants {

    private MessageConstants() {
        // Private constructor to prevent instantiation
    }

    // Approval Messages
    public static final String LOAN_APPROVED_SUCCESS = "Loan approved successfully";
    public static final String LOAN_REJECTED_SUCCESS = "Loan rejected successfully";
    public static final String LOAN_APPROVAL_NOT_FOUND = "Loan approval not found";
    public static final String LOAN_ALREADY_PROCESSED = "Loan has already been approved or rejected";

    // Disbursement Messages
    public static final String LOAN_DISBURSED_SUCCESS = "Loan disbursed successfully";
    public static final String LOAN_DISBURSEMENT_NOT_FOUND = "Loan disbursement not found";
    public static final String LOAN_ALREADY_DISBURSED = "Loan has already been disbursed";

    // Closure Messages
    public static final String LOAN_CLOSED_SUCCESS = "Loan closed successfully";
    public static final String LOAN_CLOSURE_FAILED = "Cannot close loan - not all EMIs are paid";

    // Validation Messages
    public static final String INVALID_LOAN_STATUS = "Invalid loan status for this operation";
    public static final String INSUFFICIENT_PERMISSIONS = "User does not have permission to perform this operation";
    public static final String INVALID_APPROVED_AMOUNT = "Approved amount cannot exceed requested amount";
    public static final String INVALID_INTEREST_RATE = "Interest rate must be greater than zero";
    public static final String FUTURE_DISBURSEMENT_DATE = "Disbursement date cannot be in the future";

    // Error Messages
    public static final String LOAN_NOT_FOUND = "Loan not found";
    public static final String USER_NOT_FOUND = "User not found";
    public static final String EMI_GENERATION_FAILED = "Failed to generate EMI schedule";
    public static final String LOAN_STATUS_UPDATE_FAILED = "Failed to update loan status";
}
