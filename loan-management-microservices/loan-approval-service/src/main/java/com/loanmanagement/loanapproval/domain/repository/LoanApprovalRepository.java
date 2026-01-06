package com.loanmanagement.loanapproval.domain.repository;

import com.loanmanagement.loanapproval.domain.model.LoanApproval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for LoanApproval entity
 */
@Repository
public interface LoanApprovalRepository extends JpaRepository<LoanApproval, Long> {

    /**
     * Find loan approval by loan ID
     */
    Optional<LoanApproval> findByLoanId(Long loanId);

    /**
     * Find all approvals by approver
     */
    List<LoanApproval> findByApproverId(Long approverId);

    /**
     * Find all approvals by status
     */
    List<LoanApproval> findByStatus(LoanApproval.ApprovalStatus status);

    /**
     * Check if loan has been approved/rejected
     */
    boolean existsByLoanId(Long loanId);

    /**
     * Count approvals by status
     */
    Long countByStatus(LoanApproval.ApprovalStatus status);
}
