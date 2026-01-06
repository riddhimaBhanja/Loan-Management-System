package com.loanmanagement.loanapp.domain.model;

import com.loanmanagement.loanapp.domain.enums.EmploymentStatus;
import com.loanmanagement.loanapp.domain.enums.LoanStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class LoanTest {

    @Test
    void noArgsConstructor_shouldCreateObject() {
        Loan loan = new Loan();
        assertNotNull(loan);
    }

    @Test
    void allArgsConstructor_shouldSetAllFields() {
        LocalDateTime now = LocalDateTime.now();

        Loan loan = new Loan(
                1L,
                10L,
                2L,
                3L,
                BigDecimal.valueOf(100000),
                24,
                EmploymentStatus.SALARIED,
                BigDecimal.valueOf(50000),
                "Home loan",
                LoanStatus.APPROVED,
                now,
                now,
                now
        );

        assertEquals(1L, loan.getId());
        assertEquals(10L, loan.getCustomerId());
        assertEquals(2L, loan.getLoanTypeId());
        assertEquals(3L, loan.getLoanOfficerId());
        assertEquals(BigDecimal.valueOf(100000), loan.getAmount());
        assertEquals(24, loan.getTenureMonths());
        assertEquals(EmploymentStatus.SALARIED, loan.getEmploymentStatus());
        assertEquals(BigDecimal.valueOf(50000), loan.getMonthlyIncome());
        assertEquals("Home loan", loan.getPurpose());
        assertEquals(LoanStatus.APPROVED, loan.getStatus());
        assertEquals(now, loan.getAppliedDate());
        assertEquals(now, loan.getCreatedAt());
        assertEquals(now, loan.getUpdatedAt());
    }

    @Test
    void builder_shouldApplyDefaultValues() {
        Loan loan = Loan.builder()
                .customerId(5L)
                .loanTypeId(1L)
                .amount(BigDecimal.valueOf(50000))
                .tenureMonths(12)
                .employmentStatus(EmploymentStatus.SELF_EMPLOYED)
                .monthlyIncome(BigDecimal.valueOf(40000))
                .build();

        assertEquals(LoanStatus.PENDING, loan.getStatus());
        assertNotNull(loan.getAppliedDate());
    }

    @Test
    void prePersist_shouldInitializeDefaultsWhenNull() {
        Loan loan = new Loan();
        loan.setCustomerId(1L);
        loan.setLoanTypeId(1L);
        loan.setAmount(BigDecimal.valueOf(20000));
        loan.setTenureMonths(6);
        loan.setEmploymentStatus(EmploymentStatus.UNEMPLOYED);
        loan.setMonthlyIncome(BigDecimal.valueOf(10000));

        loan.onCreate();

        assertNotNull(loan.getCreatedAt());
        assertNotNull(loan.getUpdatedAt());
        assertNotNull(loan.getAppliedDate());
        assertEquals(LoanStatus.PENDING, loan.getStatus());
    }

    @Test
    void preUpdate_shouldUpdateUpdatedAt() {
        Loan loan = new Loan();
        LocalDateTime oldTime = LocalDateTime.now().minusDays(1);
        loan.setUpdatedAt(oldTime);

        loan.onUpdate();

        assertNotNull(loan.getUpdatedAt());
        assertTrue(loan.getUpdatedAt().isAfter(oldTime));
    }
}
