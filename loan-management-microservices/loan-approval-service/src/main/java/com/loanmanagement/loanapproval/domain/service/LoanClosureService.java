package com.loanmanagement.loanapproval.domain.service;

import com.loanmanagement.common.dto.LoanDTO;

/**
 * Service interface for loan closure operations
 */
public interface LoanClosureService {

    /**
     * Close a fully paid loan
     */
    LoanDTO closeLoan(Long loanId);
}
