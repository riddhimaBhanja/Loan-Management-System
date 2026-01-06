package com.loanmanagement.loanapp.domain.repository;

import com.loanmanagement.loanapp.domain.enums.LoanStatus;
import com.loanmanagement.loanapp.domain.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Loan entity
 */
@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findByCustomerId(Long customerId);

    List<Loan> findByCustomerIdAndStatus(Long customerId, LoanStatus status);

    List<Loan> findByStatus(LoanStatus status);

    List<Loan> findByLoanTypeId(Long loanTypeId);

    List<Loan> findByLoanOfficerId(Long loanOfficerId);

    long countByCustomerId(Long customerId);

    long countByStatus(LoanStatus status);
}
