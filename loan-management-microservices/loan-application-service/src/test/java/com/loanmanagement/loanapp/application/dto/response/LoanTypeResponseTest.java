package com.loanmanagement.loanapp.application.dto.response;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class LoanTypeResponseTest {

    @Test
    void shouldCreateLoanTypeResponseUsingBuilder() {
        LocalDateTime now = LocalDateTime.now();

        LoanTypeResponse response = LoanTypeResponse.builder()
                .id(1L)
                .name("Home Loan")
                .description("Loan for purchasing a house")
                .minAmount(BigDecimal.valueOf(100000))
                .maxAmount(BigDecimal.valueOf(5000000))
                .minTenureMonths(12)
                .maxTenureMonths(360)
                .interestRate(BigDecimal.valueOf(8.5))
                .lateFeePercentage(BigDecimal.valueOf(2.0))
                .gracePeriodDays(3)
                .isActive(true)
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertEquals(1L, response.getId());
        assertEquals("Home Loan", response.getName());
        assertEquals("Loan for purchasing a house", response.getDescription());
        assertEquals(BigDecimal.valueOf(100000), response.getMinAmount());
        assertEquals(BigDecimal.valueOf(5000000), response.getMaxAmount());
        assertEquals(12, response.getMinTenureMonths());
        assertEquals(360, response.getMaxTenureMonths());
        assertEquals(BigDecimal.valueOf(8.5), response.getInterestRate());
        assertEquals(BigDecimal.valueOf(2.0), response.getLateFeePercentage());
        assertEquals(3, response.getGracePeriodDays());
        assertTrue(response.getIsActive());
        assertEquals(now, response.getCreatedAt());
        assertEquals(now, response.getUpdatedAt());
    }

    @Test
    void shouldSupportNoArgsConstructor() {
        LoanTypeResponse response = new LoanTypeResponse();

        assertNull(response.getId());
        assertNull(response.getName());
        assertNull(response.getDescription());
        assertNull(response.getMinAmount());
        assertNull(response.getMaxAmount());
        assertNull(response.getMinTenureMonths());
        assertNull(response.getMaxTenureMonths());
        assertNull(response.getInterestRate());
        assertNull(response.getLateFeePercentage());
        assertNull(response.getGracePeriodDays());
        assertNull(response.getIsActive());
        assertNull(response.getCreatedAt());
        assertNull(response.getUpdatedAt());
    }

    @Test
    void shouldSupportAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();

        LoanTypeResponse response = new LoanTypeResponse(
                2L,
                "Car Loan",
                "Loan for buying a car",
                BigDecimal.valueOf(50000),
                BigDecimal.valueOf(1000000),
                6,
                84,
                BigDecimal.valueOf(9.5),
                BigDecimal.valueOf(1.5),
                5,
                false,
                now,
                now
        );

        assertEquals(2L, response.getId());
        assertEquals("Car Loan", response.getName());
        assertEquals("Loan for buying a car", response.getDescription());
        assertEquals(BigDecimal.valueOf(50000), response.getMinAmount());
        assertEquals(BigDecimal.valueOf(1000000), response.getMaxAmount());
        assertEquals(6, response.getMinTenureMonths());
        assertEquals(84, response.getMaxTenureMonths());
        assertEquals(BigDecimal.valueOf(9.5), response.getInterestRate());
        assertEquals(BigDecimal.valueOf(1.5), response.getLateFeePercentage());
        assertEquals(5, response.getGracePeriodDays());
        assertFalse(response.getIsActive());
        assertEquals(now, response.getCreatedAt());
        assertEquals(now, response.getUpdatedAt());
    }
}
