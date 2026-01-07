package com.loanmanagement.loanapproval.domain.repository;

import com.loanmanagement.loanapproval.domain.model.LoanApproval;
import com.loanmanagement.loanapproval.domain.model.LoanApproval.ApprovalStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Test cases for LoanApprovalRepository
 */
@ExtendWith(MockitoExtension.class)
class LoanApprovalRepositoryTest {

    @Mock
    private LoanApprovalRepository loanApprovalRepository;

    @Test
    void findByLoanId_success() {
        Long loanId = 1L;
        LoanApproval loanApproval = new LoanApproval();

        when(loanApprovalRepository.findByLoanId(loanId))
                .thenReturn(Optional.of(loanApproval));

        Optional<LoanApproval> result =
                loanApprovalRepository.findByLoanId(loanId);

        assertTrue(result.isPresent());
        assertEquals(loanApproval, result.get());
    }

    @Test
    void findByLoanId_notFound() {
        Long loanId = 2L;

        when(loanApprovalRepository.findByLoanId(loanId))
                .thenReturn(Optional.empty());

        Optional<LoanApproval> result =
                loanApprovalRepository.findByLoanId(loanId);

        assertFalse(result.isPresent());
    }

    @Test
    void findByApproverId_success() {
        Long approverId = 10L;
        List<LoanApproval> approvals =
                Collections.singletonList(new LoanApproval());

        when(loanApprovalRepository.findByApproverId(approverId))
                .thenReturn(approvals);

        List<LoanApproval> result =
                loanApprovalRepository.findByApproverId(approverId);

        assertEquals(1, result.size());
    }

    @Test
    void findByStatus_success() {
        ApprovalStatus status = ApprovalStatus.APPROVED;
        List<LoanApproval> approvals =
                Collections.singletonList(new LoanApproval());

        when(loanApprovalRepository.findByStatus(status))
                .thenReturn(approvals);

        List<LoanApproval> result =
                loanApprovalRepository.findByStatus(status);

        assertEquals(1, result.size());
    }

    @Test
    void existsByLoanId_returnsTrue() {
        Long loanId = 3L;

        when(loanApprovalRepository.existsByLoanId(loanId))
                .thenReturn(true);

        boolean exists =
                loanApprovalRepository.existsByLoanId(loanId);

        assertTrue(exists);
    }

    @Test
    void existsByLoanId_returnsFalse() {
        Long loanId = 4L;

        when(loanApprovalRepository.existsByLoanId(loanId))
                .thenReturn(false);

        boolean exists =
                loanApprovalRepository.existsByLoanId(loanId);

        assertFalse(exists);
    }

    @Test
    void countByStatus_success() {
        ApprovalStatus status = ApprovalStatus.REJECTED;

        when(loanApprovalRepository.countByStatus(status))
                .thenReturn(5L);

        Long count =
                loanApprovalRepository.countByStatus(status);

        assertEquals(5L, count);
    }
}
