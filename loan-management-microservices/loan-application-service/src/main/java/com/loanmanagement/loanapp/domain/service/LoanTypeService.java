package com.loanmanagement.loanapp.domain.service;

import com.loanmanagement.loanapp.application.dto.request.CreateLoanTypeRequest;
import com.loanmanagement.loanapp.application.dto.request.UpdateLoanTypeRequest;
import com.loanmanagement.loanapp.application.dto.response.LoanTypeResponse;

import java.util.List;

/**
 * Service interface for LoanType operations
 */
public interface LoanTypeService {

    LoanTypeResponse createLoanType(CreateLoanTypeRequest request);

    LoanTypeResponse getLoanTypeById(Long id);

    List<LoanTypeResponse> getAllLoanTypes();

    List<LoanTypeResponse> getActiveLoanTypes();

    LoanTypeResponse updateLoanType(Long id, UpdateLoanTypeRequest request);

    void deleteLoanType(Long id);
}
