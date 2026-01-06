package com.loanmanagement.loanapproval.domain.repository;

import com.loanmanagement.loanapproval.domain.model.LoanDisbursement;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Test cases for LoanDisbursementRepository
 */
@ExtendWith(MockitoExtension.class)
class LoanDisbursementRepositoryTest {

    @Mock
    private LoanDisbursementRepository loanDisbursementRepository;

    @Test
    void findByLoanId_success() {
        Long loanId = 1L;
        LoanDisbursement loanDisbursement = new LoanDisbursement();

        when(loanDisbursementRepository.findByLoanId(loanId))
                .thenReturn(Optional.of(loanDisbursement));

        Optional<LoanDisbursement> result =
                loanDisbursementRepository.findByLoanId(loanId);

        assertTrue(result.isPresent());
        assertEquals(loanDisbursement, result.get());
    }

    @Test
    void findByLoanId_notFound() {
        Long loanId = 2L;

        when(loanDisbursementRepository.findByLoanId(loanId))
                .thenReturn(Optional.empty());

        Optional<LoanDisbursement> result =
                loanDisbursementRepository.findByLoanId(loanId);

        assertFalse(result.isPresent());
    }

    @Test
    void findByDisbursedBy_success() {
        Long disbursedBy = 10L;
        List<LoanDisbursement> disbursements =
                Collections.singletonList(new LoanDisbursement());

        when(loanDisbursementRepository.findByDisbursedBy(disbursedBy))
                .thenReturn(disbursements);

        List<LoanDisbursement> result =
                loanDisbursementRepository.findByDisbursedBy(disbursedBy);

        assertEquals(1, result.size());
    }

    @Test
    void existsByLoanId_returnsTrue() {
        Long loanId = 3L;

        when(loanDisbursementRepository.existsByLoanId(loanId))
                .thenReturn(true);

        boolean exists =
                loanDisbursementRepository.existsByLoanId(loanId);

        assertTrue(exists);
    }

    @Test
    void existsByLoanId_returnsFalse() {
        Long loanId = 4L;

        when(loanDisbursementRepository.existsByLoanId(loanId))
                .thenReturn(false);

        boolean exists =
                loanDisbursementRepository.existsByLoanId(loanId);

        assertFalse(exists);
    }

    @Test
    void findByReferenceNumber_success() {
        String referenceNumber = "REF123";
        LoanDisbursement loanDisbursement = new LoanDisbursement();

        when(loanDisbursementRepository.findByReferenceNumber(referenceNumber))
                .thenReturn(Optional.of(loanDisbursement));

        Optional<LoanDisbursement> result =
                loanDisbursementRepository.findByReferenceNumber(referenceNumber);

        assertTrue(result.isPresent());
        assertEquals(loanDisbursement, result.get());
    }

    @Test
    void findByReferenceNumber_notFound() {
        String referenceNumber = "REF999";

        when(loanDisbursementRepository.findByReferenceNumber(referenceNumber))
                .thenReturn(Optional.empty());

        Optional<LoanDisbursement> result =
                loanDisbursementRepository.findByReferenceNumber(referenceNumber);

        assertFalse(result.isPresent());
    }
}
