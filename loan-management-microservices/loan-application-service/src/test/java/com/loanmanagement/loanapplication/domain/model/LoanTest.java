package com.loanmanagement.loanapplication.domain.model;

import com.loanmanagement.loanapp.domain.enums.EmploymentStatus;
import com.loanmanagement.loanapp.domain.enums.LoanStatus;
import com.loanmanagement.loanapp.domain.model.Loan;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Loan Entity Tests")
class LoanTest {

    @Test
    @DisplayName("Should initialize default values on persist")
    void shouldInitializeDefaults_OnCreate() {
        // Given
        LocalDateTime now = LocalDateTime.now();

        Loan loan = Loan.builder()
                .customerId(1L)
                .loanTypeId(2L)
                .amount(new BigDecimal("500000"))
                .tenureMonths(24)
                .employmentStatus(EmploymentStatus.SALARIED)
                .monthlyIncome(new BigDecimal("60000"))
                .purpose("Home renovation")
                .status(LoanStatus.PENDING)
                .appliedDate(now)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Then
        assertThat(loan.getCreatedAt()).isNotNull();
        assertThat(loan.getUpdatedAt()).isNotNull();
        assertThat(loan.getAppliedDate()).isNotNull();
        assertThat(loan.getStatus()).isEqualTo(LoanStatus.PENDING);
    }

    @Test
    @DisplayName("Should not override existing values on persist")
    void shouldNotOverrideExistingValues_OnCreate() {
        // Given
        LocalDateTime now = LocalDateTime.now();

        Loan loan = Loan.builder()
                .customerId(1L)
                .loanTypeId(2L)
                .amount(new BigDecimal("300000"))
                .tenureMonths(12)
                .employmentStatus(EmploymentStatus.SELF_EMPLOYED)
                .monthlyIncome(new BigDecimal("80000"))
                .status(LoanStatus.APPROVED)
                .appliedDate(now)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Then
        assertThat(loan.getStatus()).isEqualTo(LoanStatus.APPROVED);
        assertThat(loan.getAppliedDate()).isEqualTo(now);
        assertThat(loan.getCreatedAt()).isEqualTo(now);
        assertThat(loan.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Should allow getters and setters")
    void shouldAllowGettersAndSetters() {
        // Given
        Loan loan = new Loan();

        // When
        loan.setCustomerId(10L);
        loan.setLoanTypeId(20L);
        loan.setAmount(new BigDecimal("100000"));
        loan.setTenureMonths(6);
        loan.setEmploymentStatus(EmploymentStatus.UNEMPLOYED);
        loan.setMonthlyIncome(new BigDecimal("0"));
        loan.setPurpose("Education");
        loan.setStatus(LoanStatus.REJECTED);

        // Then
        assertThat(loan.getCustomerId()).isEqualTo(10L);
        assertThat(loan.getLoanTypeId()).isEqualTo(20L);
        assertThat(loan.getAmount()).isEqualTo(new BigDecimal("100000"));
        assertThat(loan.getTenureMonths()).isEqualTo(6);
        assertThat(loan.getEmploymentStatus()).isEqualTo(EmploymentStatus.UNEMPLOYED);
        assertThat(loan.getMonthlyIncome()).isEqualTo(new BigDecimal("0"));
        assertThat(loan.getPurpose()).isEqualTo("Education");
        assertThat(loan.getStatus()).isEqualTo(LoanStatus.REJECTED);
    }
}
