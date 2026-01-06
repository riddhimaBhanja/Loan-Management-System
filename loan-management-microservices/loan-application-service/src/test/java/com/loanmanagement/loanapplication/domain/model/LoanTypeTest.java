package com.loanmanagement.loanapplication.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.loanmanagement.loanapp.domain.model.LoanType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("LoanType Entity Tests")
class LoanTypeTest {

    @Test
    @DisplayName("Should create LoanType with audit fields")
    void shouldCreateLoanTypeWithAuditFields() {
        LocalDateTime now = LocalDateTime.now();

        LoanType loanType = LoanType.builder()
                .name("Home Loan")
                .description("Housing loan")
                .minAmount(new BigDecimal("100000"))
                .maxAmount(new BigDecimal("1000000"))
                .minTenureMonths(12)
                .maxTenureMonths(240)
                .interestRate(new BigDecimal("8.50"))
                .isActive(true)
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertThat(loanType.getCreatedAt()).isNotNull();
        assertThat(loanType.getUpdatedAt()).isNotNull();
        assertThat(loanType.getIsActive()).isTrue();
    }

    @Test
    @DisplayName("Should have updated timestamp after created timestamp")
    void shouldHaveUpdatedTimestampAfterCreatedTimestamp() {
        LocalDateTime created = LocalDateTime.now().minusDays(1);
        LocalDateTime updated = LocalDateTime.now();

        LoanType loanType = LoanType.builder()
                .name("Car Loan")
                .description("Vehicle loan")
                .minAmount(new BigDecimal("50000"))
                .maxAmount(new BigDecimal("500000"))
                .minTenureMonths(6)
                .maxTenureMonths(84)
                .interestRate(new BigDecimal("9.25"))
                .createdAt(created)
                .updatedAt(updated)
                .build();

        assertThat(loanType.getUpdatedAt()).isAfter(loanType.getCreatedAt());
    }

    @Test
    @DisplayName("Should validate amount within range")
    void shouldValidateAmountCorrectly() {
        LoanType loanType = LoanType.builder()
                .minAmount(new BigDecimal("100000"))
                .maxAmount(new BigDecimal("1000000"))
                .build();

        assertThat(loanType.isAmountValid(new BigDecimal("100000"))).isTrue();
        assertThat(loanType.isAmountValid(new BigDecimal("500000"))).isTrue();
        assertThat(loanType.isAmountValid(new BigDecimal("1000000"))).isTrue();
        assertThat(loanType.isAmountValid(new BigDecimal("99999"))).isFalse();
        assertThat(loanType.isAmountValid(new BigDecimal("1000001"))).isFalse();
    }

    @Test
    @DisplayName("Should validate tenure within range")
    void shouldValidateTenureCorrectly() {
        LoanType loanType = LoanType.builder()
                .minTenureMonths(12)
                .maxTenureMonths(240)
                .build();

        assertThat(loanType.isTenureValid(12)).isTrue();
        assertThat(loanType.isTenureValid(120)).isTrue();
        assertThat(loanType.isTenureValid(240)).isTrue();
        assertThat(loanType.isTenureValid(11)).isFalse();
        assertThat(loanType.isTenureValid(241)).isFalse();
    }

    @Test
    @DisplayName("Should handle minimal valid LoanType")
    void shouldHandleMinimalLoanType() {
        LocalDateTime now = LocalDateTime.now();

        LoanType loanType = LoanType.builder()
                .name("Personal Loan")
                .minAmount(new BigDecimal("10000"))
                .maxAmount(new BigDecimal("200000"))
                .minTenureMonths(6)
                .maxTenureMonths(60)
                .interestRate(new BigDecimal("12.00"))
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertThat(loanType.getName()).isEqualTo("Personal Loan");
        assertThat(loanType.getCreatedAt()).isNotNull();
        assertThat(loanType.getUpdatedAt()).isNotNull();
    }
}
