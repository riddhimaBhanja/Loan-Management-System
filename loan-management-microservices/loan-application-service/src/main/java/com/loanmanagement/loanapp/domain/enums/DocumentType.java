package com.loanmanagement.loanapp.domain.enums;

/**
 * Enumeration for document types
 */
public enum DocumentType {
    ID_PROOF,           // Aadhaar, PAN, Passport, Voter ID
    INCOME_PROOF,       // Salary Slips, IT Returns, Form 16
    ADDRESS_PROOF,      // Utility Bills, Rental Agreement
    BANK_STATEMENT,     // Bank statements for last 6 months
    EMPLOYMENT_PROOF,   // Employment letter, offer letter
    BUSINESS_PROOF,     // GST Certificate, Business license
    OTHER               // Other documents
}
