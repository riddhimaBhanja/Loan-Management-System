package com.loanmanagement.loanapp.domain.repository;

import com.loanmanagement.loanapp.domain.enums.EmploymentStatus;
import com.loanmanagement.loanapp.domain.enums.LoanStatus;
import com.loanmanagement.loanapp.domain.model.Loan;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
class LoanRepositoryTest {

    @Autowired
    private LoanRepository loanRepository;

    private Loan createLoan(Long customerId, LoanStatus status, Long officerId) {
        return Loan.builder()
                .customerId(customerId)
                .loanTypeId(1L)
                .loanOfficerId(officerId)
                .amount(BigDecimal.valueOf(50000))
                .tenureMonths(12)
                .employmentStatus(EmploymentStatus.SALARIED)
                .monthlyIncome(BigDecimal.valueOf(40000))
                .purpose("Test Loan")
                .status(status)
                .appliedDate(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void findByCustomerId_shouldReturnLoans() {
        loanRepository.save(createLoan(1L, LoanStatus.PENDING, 10L));
        loanRepository.save(createLoan(1L, LoanStatus.APPROVED, 11L));

        List<Loan> loans = loanRepository.findByCustomerId(1L);

        assertEquals(2, loans.size());
    }

    @Test
    void findByCustomerIdAndStatus_shouldReturnMatchingLoans() {
        loanRepository.save(createLoan(2L, LoanStatus.APPROVED, 10L));
        loanRepository.save(createLoan(2L, LoanStatus.REJECTED, 10L));

        List<Loan> loans =
                loanRepository.findByCustomerIdAndStatus(2L, LoanStatus.APPROVED);

        assertEquals(1, loans.size());
        assertEquals(LoanStatus.APPROVED, loans.get(0).getStatus());
    }

    @Test
    void findByStatus_shouldReturnLoans() {
        loanRepository.save(createLoan(3L, LoanStatus.PENDING, 10L));
        loanRepository.save(createLoan(4L, LoanStatus.PENDING, 11L));

        List<Loan> loans = loanRepository.findByStatus(LoanStatus.PENDING);

        assertEquals(2, loans.size());
    }

    @Test
    void findByLoanTypeId_shouldReturnLoans() {
        Loan loan = createLoan(5L, LoanStatus.PENDING, 10L);
        loan.setLoanTypeId(99L);
        loanRepository.save(loan);

        List<Loan> loans = loanRepository.findByLoanTypeId(99L);

        assertEquals(1, loans.size());
    }

    @Test
    void findByLoanOfficerId_shouldReturnLoans() {
        loanRepository.save(createLoan(6L, LoanStatus.APPROVED, 50L));

        List<Loan> loans = loanRepository.findByLoanOfficerId(50L);

        assertEquals(1, loans.size());
    }

    @Test
    void countByCustomerId_shouldReturnCorrectCount() {
        loanRepository.save(createLoan(7L, LoanStatus.PENDING, 10L));
        loanRepository.save(createLoan(7L, LoanStatus.APPROVED, 11L));

        long count = loanRepository.countByCustomerId(7L);

        assertEquals(2, count);
    }

    @Test
    void countByStatus_shouldReturnCorrectCount() {
        loanRepository.save(createLoan(8L, LoanStatus.REJECTED, 10L));
        loanRepository.save(createLoan(9L, LoanStatus.REJECTED, 11L));

        long count = loanRepository.countByStatus(LoanStatus.REJECTED);

        assertEquals(2, count);
    }
}
