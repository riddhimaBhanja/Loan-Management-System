package com.loanmanagement.loanapp.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class LoanTypeTest {

    @Test
    void noArgsConstructor_shouldCreateObject() {
        LoanType loanType = new LoanType();
        assertNotNull(loanType);
    }

    @Test
    void allArgsConstructor_shouldSetAllFields() {
        LocalDateTime now = LocalDateTime.now();

        LoanType loanType = new LoanType(
                1L,
                "Home Loan",
                "Loan for home purchase",
                BigDecimal.valueOf(100000),
                BigDecimal.valueOf(1000000),
                12,
                240,
                BigDecimal.valueOf(8.5),
                BigDecimal.valueOf(2.5),
                5,
                true,
                now,
                now
        );

        assertEquals(1L, loanType.getId());
        assertEquals("Home Loan", loanType.getName());
        assertEquals("Loan for home purchase", loanType.getDescription());
        assertEquals(BigDecimal.valueOf(100000), loanType.getMinAmount());
        assertEquals(BigDecimal.valueOf(1000000), loanType.getMaxAmount());
        assertEquals(12, loanType.getMinTenureMonths());
        assertEquals(240, loanType.getMaxTenureMonths());
        assertEquals(BigDecimal.valueOf(8.5), loanType.getInterestRate());
        assertEquals(BigDecimal.valueOf(2.5), loanType.getLateFeePercentage());
        assertEquals(5, loanType.getGracePeriodDays());
        assertTrue(loanType.getIsActive());
        assertEquals(now, loanType.getCreatedAt());
        assertEquals(now, loanType.getUpdatedAt());
    }

    @Test
    void builder_shouldApplyDefaultValues() {
        LoanType loanType = LoanType.builder()
                .name("Personal Loan")
                .minAmount(BigDecimal.valueOf(5000))
                .maxAmount(BigDecimal.valueOf(50000))
                .minTenureMonths(6)
                .maxTenureMonths(60)
                .interestRate(BigDecimal.valueOf(12.5))
                .build();

        assertEquals(BigDecimal.valueOf(2.0), loanType.getLateFeePercentage());
        assertEquals(3, loanType.getGracePeriodDays());
        assertTrue(loanType.getIsActive());
    }

    @Test
    void prePersist_shouldInitializeTimestamps() {
        LoanType loanType = new LoanType();
        loanType.setName("Car Loan");
        loanType.setMinAmount(BigDecimal.valueOf(10000));
        loanType.setMaxAmount(BigDecimal.valueOf(500000));
        loanType.setMinTenureMonths(12);
        loanType.setMaxTenureMonths(84);
        loanType.setInterestRate(BigDecimal.valueOf(9.0));

        loanType.onCreate();

        assertNotNull(loanType.getCreatedAt());
        assertNotNull(loanType.getUpdatedAt());
    }

    @Test
    void preUpdate_shouldUpdateUpdatedAt() {
        LoanType loanType = new LoanType();
        LocalDateTime oldTime = LocalDateTime.now().minusDays(1);
        loanType.setUpdatedAt(oldTime);

        loanType.onUpdate();

        assertNotNull(loanType.getUpdatedAt());
        assertTrue(loanType.getUpdatedAt().isAfter(oldTime));
    }

    @Test
    void isAmountValid_shouldValidateCorrectly() {
        LoanType loanType = LoanType.builder()
                .minAmount(BigDecimal.valueOf(10000))
                .maxAmount(BigDecimal.valueOf(50000))
                .build();

        assertTrue(loanType.isAmountValid(BigDecimal.valueOf(10000)));
        assertTrue(loanType.isAmountValid(BigDecimal.valueOf(30000)));
        assertTrue(loanType.isAmountValid(BigDecimal.valueOf(50000)));
        assertFalse(loanType.isAmountValid(BigDecimal.valueOf(9000)));
        assertFalse(loanType.isAmountValid(BigDecimal.valueOf(60000)));
    }

    @Test
    void isTenureValid_shouldValidateCorrectly() {
        LoanType loanType = LoanType.builder()
                .minTenureMonths(6)
                .maxTenureMonths(60)
                .build();

        assertTrue(loanType.isTenureValid(6));
        assertTrue(loanType.isTenureValid(36));
        assertTrue(loanType.isTenureValid(60));
        assertFalse(loanType.isTenureValid(3));
        assertFalse(loanType.isTenureValid(72));
    }
}
