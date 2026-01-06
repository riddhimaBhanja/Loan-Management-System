package com.loanmanagement.loanapproval.domain.repository;

import com.loanmanagement.loanapproval.domain.model.LoanDisbursement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for LoanDisbursement entity
 */
@Repository
public interface LoanDisbursementRepository extends JpaRepository<LoanDisbursement, Long> {

    /**
     * Find disbursement by loan ID
     */
    Optional<LoanDisbursement> findByLoanId(Long loanId);

    /**
     * Find all disbursements by user
     */
    List<LoanDisbursement> findByDisbursedBy(Long disbursedBy);

    /**
     * Check if loan has been disbursed
     */
    boolean existsByLoanId(Long loanId);

    /**
     * Find by reference number
     */
    Optional<LoanDisbursement> findByReferenceNumber(String referenceNumber);
}
