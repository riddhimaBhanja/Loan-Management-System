package com.loanmanagement.loanapp.domain.service;

import com.loanmanagement.loanapp.application.dto.request.AssignOfficerRequest;
import com.loanmanagement.loanapp.application.dto.request.LoanApplicationRequest;
import com.loanmanagement.loanapp.application.dto.response.LoanResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for Loan Application operations
 */
public interface LoanApplicationService {

    LoanResponse createLoanApplication(LoanApplicationRequest request, Long customerId);

    LoanResponse getLoanById(Long id);

    List<LoanResponse> getCustomerLoans(Long customerId);

    List<LoanResponse> getAllLoans();

    List<LoanResponse> getLoansByStatus(String status);

    LoanResponse assignOfficer(Long loanId, AssignOfficerRequest request);

    LoanResponse unassignOfficer(Long loanId);

    List<LoanResponse> getLoansByOfficerId(Long officerId);

    // Internal service-to-service methods
    void approveLoan(Long loanId, BigDecimal approvedAmount, BigDecimal interestRate);

    void rejectLoan(Long loanId, String reason);

    void disburseLoan(Long loanId, LocalDate disbursementDate, String disbursementMethod, String referenceNumber);

    void closeLoan(Long loanId);
}
