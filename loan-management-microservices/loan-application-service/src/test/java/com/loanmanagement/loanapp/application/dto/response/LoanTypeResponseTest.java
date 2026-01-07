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
    @Test
    void equalsAndHashCode_shouldWorkCorrectly() {
        LoanTypeResponse r1 = LoanTypeResponse.builder()
                .id(1L)
                .name("Home Loan")
                .interestRate(BigDecimal.valueOf(8.5))
                .isActive(true)
                .build();

        LoanTypeResponse r2 = LoanTypeResponse.builder()
                .id(1L)
                .name("Home Loan")
                .interestRate(BigDecimal.valueOf(8.5))
                .isActive(true)
                .build();

        LoanTypeResponse r3 = LoanTypeResponse.builder()
                .id(2L)
                .name("Car Loan")
                .build();

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
        assertNotEquals(r1, r3);
    }
    @Test
    void equals_shouldReturnFalseForNullAndDifferentType() {
        LoanTypeResponse response = LoanTypeResponse.builder()
                .id(1L)
                .build();

        assertNotEquals(response, null);
        assertNotEquals(response, "not-a-loan-type-response");
    }
    @Test
    void toString_shouldContainClassNameAndKeyFields() {
        LoanTypeResponse response = LoanTypeResponse.builder()
                .id(1L)
                .name("Home Loan")
                .build();

        String value = response.toString();

        assertNotNull(value);
        assertTrue(value.contains("LoanTypeResponse"));
        assertTrue(value.contains("Home Loan"));
    }
    @Test
    void settersAndGetters_shouldWorkCorrectly() {
        LoanTypeResponse response = new LoanTypeResponse();

        response.setId(10L);
        response.setName("Education Loan");
        response.setDescription("Loan for studies");
        response.setMinAmount(BigDecimal.valueOf(20000));
        response.setMaxAmount(BigDecimal.valueOf(800000));
        response.setMinTenureMonths(6);
        response.setMaxTenureMonths(120);
        response.setInterestRate(BigDecimal.valueOf(7.5));
        response.setLateFeePercentage(BigDecimal.valueOf(1.0));
        response.setGracePeriodDays(10);
        response.setIsActive(false);

        assertEquals(10L, response.getId());
        assertEquals("Education Loan", response.getName());
        assertEquals("Loan for studies", response.getDescription());
        assertEquals(BigDecimal.valueOf(20000), response.getMinAmount());
        assertEquals(BigDecimal.valueOf(800000), response.getMaxAmount());
        assertEquals(6, response.getMinTenureMonths());
        assertEquals(120, response.getMaxTenureMonths());
        assertEquals(BigDecimal.valueOf(7.5), response.getInterestRate());
        assertEquals(BigDecimal.valueOf(1.0), response.getLateFeePercentage());
        assertEquals(10, response.getGracePeriodDays());
        assertFalse(response.getIsActive());
    }

    @Test
    void equals_shouldReturnFalseWhenFieldsDiffer() {
        LoanTypeResponse r1 = LoanTypeResponse.builder()
                .id(1L)
                .name("Home Loan")
                .interestRate(BigDecimal.valueOf(8.5))
                .build();

        LoanTypeResponse r2 = LoanTypeResponse.builder()
                .id(1L)
                .name("Home Loan")
                .interestRate(BigDecimal.valueOf(9.0)) // different
                .build();

        assertNotEquals(r1, r2);
    }

    @Test
    void equals_shouldReturnFalseWhenOneFieldIsNull() {
        LoanTypeResponse r1 = LoanTypeResponse.builder()
                .id(1L)
                .name("Home Loan")
                .build();

        LoanTypeResponse r2 = LoanTypeResponse.builder()
                .id(1L)
                .name(null)
                .build();

        assertNotEquals(r1, r2);
    }

    @Test
    void canEqual_shouldReturnTrueForSameType() {
        LoanTypeResponse r1 = new LoanTypeResponse();
        LoanTypeResponse r2 = new LoanTypeResponse();

        assertTrue(r1.canEqual(r2));
    }

    @Test
    void canEqual_shouldReturnFalseForDifferentType() {
        LoanTypeResponse response = new LoanTypeResponse();

        assertFalse(response.canEqual("not-a-loan-type-response"));
    }

    @Test
    void hashCode_shouldBeStableAcrossCalls() {
        LoanTypeResponse response = LoanTypeResponse.builder()
                .id(99L)
                .name("Test Loan")
                .build();

        int hash1 = response.hashCode();
        int hash2 = response.hashCode();

        assertEquals(hash1, hash2);
    }


}
