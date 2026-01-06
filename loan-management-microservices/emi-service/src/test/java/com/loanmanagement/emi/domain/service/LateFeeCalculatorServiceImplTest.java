package com.loanmanagement.emi.domain.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class LateFeeCalculationServiceImplTest {

    private LateFeeCalculationServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new LateFeeCalculationServiceImpl();
    }

    @Test
    void calculateLateFee_nullInputs_returnsZero() {
        BigDecimal fee = service.calculateLateFee(
                null,
                null,
                null,
                new BigDecimal("2"),
                3
        );
        assertEquals(BigDecimal.ZERO, fee);
    }

    @Test
    void calculateLateFee_noLateFeePercentage_returnsZero() {
        BigDecimal fee = service.calculateLateFee(
                new BigDecimal("5000"),
                LocalDate.now(),
                LocalDate.now().plusDays(5),
                BigDecimal.ZERO,
                3
        );
        assertEquals(BigDecimal.ZERO, fee);
    }

    @Test
    void calculateLateFee_withinGracePeriod_returnsZero() {
        LocalDate dueDate = LocalDate.of(2026, 1, 1);
        LocalDate paymentDate = LocalDate.of(2026, 1, 3);

        BigDecimal fee = service.calculateLateFee(
                new BigDecimal("5000"),
                dueDate,
                paymentDate,
                new BigDecimal("2"),
                3
        );

        assertEquals(BigDecimal.ZERO, fee);
    }

    @Test
    void calculateLateFee_afterGracePeriod_success() {
        LocalDate dueDate = LocalDate.of(2026, 1, 1);
        LocalDate paymentDate = LocalDate.of(2026, 1, 6); // 5 days late

        BigDecimal fee = service.calculateLateFee(
                new BigDecimal("5000"),
                dueDate,
                paymentDate,
                new BigDecimal("2"),
                3 // grace = 3 → chargeable days = 2
        );

        assertEquals(new BigDecimal("200.00"), fee);
    }

    @Test
    void getChargeableLateDays_beforeDueDate_returnsZero() {
        long days = service.getChargeableLateDays(
                LocalDate.of(2026, 1, 10),
                LocalDate.of(2026, 1, 9),
                3
        );
        assertEquals(0, days);
    }

    @Test
    void getChargeableLateDays_afterGracePeriod_success() {
        long days = service.getChargeableLateDays(
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 6), // 5 days late
                3
        );
        assertEquals(2, days);
    }

    @Test
    void isPaymentLate_withinGrace_returnsFalse() {
        boolean late = service.isPaymentLate(
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 3),
                3
        );
        assertFalse(late);
    }

    @Test
    void isPaymentLate_afterGrace_returnsTrue() {
        boolean late = service.isPaymentLate(
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 6),
                3
        );
        assertTrue(late);
    }

    @Test
    void isPaymentLate_nullDates_returnsFalse() {
        assertFalse(service.isPaymentLate(null, null, 3));
    }
}
