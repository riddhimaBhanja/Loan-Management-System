package com.loanmanagement.loanapp.shared.constants;

/**
 * Message constants for the application
 */
public class MessageConstants {

    // Loan Type Messages
    public static final String LOAN_TYPE_CREATED = "Loan type created successfully";
    public static final String LOAN_TYPE_UPDATED = "Loan type updated successfully";
    public static final String LOAN_TYPE_DELETED = "Loan type deleted successfully";
    public static final String LOAN_TYPE_FETCHED = "Loan type fetched successfully";
    public static final String LOAN_TYPES_FETCHED = "Loan types fetched successfully";

    // Loan Application Messages
    public static final String LOAN_APPLICATION_CREATED = "Loan application submitted successfully";
    public static final String LOAN_FETCHED = "Loan fetched successfully";
    public static final String LOANS_FETCHED = "Loans fetched successfully";

    // Document Messages
    public static final String DOCUMENT_UPLOADED = "Document uploaded successfully";
    public static final String DOCUMENT_DELETED = "Document deleted successfully";
    public static final String DOCUMENTS_FETCHED = "Documents fetched successfully";

    private MessageConstants() {
        // Private constructor to prevent instantiation
    }
}
