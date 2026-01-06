package com.loanmanagement.loanapproval.domain.service;

import com.loanmanagement.loanapproval.application.dto.request.DisburseLoanRequest;
import com.loanmanagement.loanapproval.application.dto.response.LoanDisbursementResponse;

/**
 * Service interface for loan disbursement operations
 */
public interface LoanDisbursementService {

    /**
     * Disburse an approved loan
     */
    LoanDisbursementResponse disburseLoan(Long loanId, DisburseLoanRequest request, Long disbursedBy);

    /**
     * Get disbursement details by loan ID
     */
    LoanDisbursementResponse getDisbursementByLoanId(Long loanId);
}
