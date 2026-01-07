package com.loanmanagement.loanapproval.application.dto.response;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class LoanDisbursementResponseTest {

    @Test
    void builderAndGetters_shouldWorkCorrectly() {
        LocalDateTime now = LocalDateTime.now();
        LocalDate disbursementDate = LocalDate.now();

        LoanDisbursementResponse response = LoanDisbursementResponse.builder()
                .id(1L)
                .loanId(10L)
                .disbursedBy(100L)
                .disbursedByName("Loan Officer")
                .amount(new BigDecimal("50000.00"))
                .disbursementDate(disbursementDate)
                .disbursementMethod("BANK_TRANSFER")
                .referenceNumber("TXN123456")
                .remarks("Disbursed successfully")
                .createdAt(now)
                .build();

        assertEquals(1L, response.getId());
        assertEquals(10L, response.getLoanId());
        assertEquals(100L, response.getDisbursedBy());
        assertEquals("Loan Officer", response.getDisbursedByName());
        assertEquals(new BigDecimal("50000.00"), response.getAmount());
        assertEquals(disbursementDate, response.getDisbursementDate());
        assertEquals("BANK_TRANSFER", response.getDisbursementMethod());
        assertEquals("TXN123456", response.getReferenceNumber());
        assertEquals("Disbursed successfully", response.getRemarks());
        assertEquals(now, response.getCreatedAt());
    }

    @Test
    void noArgsConstructorAndSetters_shouldWorkCorrectly() {
        LocalDateTime now = LocalDateTime.now();
        LocalDate disbursementDate = LocalDate.now();

        LoanDisbursementResponse response = new LoanDisbursementResponse();
        response.setId(2L);
        response.setLoanId(20L);
        response.setDisbursedBy(200L);
        response.setDisbursedByName("Admin");
        response.setAmount(new BigDecimal("75000.00"));
        response.setDisbursementDate(disbursementDate);
        response.setDisbursementMethod("CHEQUE");
        response.setReferenceNumber("REF987654");
        response.setRemarks("Manual disbursement");
        response.setCreatedAt(now);

        assertEquals(2L, response.getId());
        assertEquals(20L, response.getLoanId());
        assertEquals(200L, response.getDisbursedBy());
        assertEquals("Admin", response.getDisbursedByName());
        assertEquals(new BigDecimal("75000.00"), response.getAmount());
        assertEquals(disbursementDate, response.getDisbursementDate());
        assertEquals("CHEQUE", response.getDisbursementMethod());
        assertEquals("REF987654", response.getReferenceNumber());
        assertEquals("Manual disbursement", response.getRemarks());
        assertEquals(now, response.getCreatedAt());
    }
    @Test
    void equalsAndHashCode_shouldWorkCorrectly() {
        LocalDateTime now = LocalDateTime.now();
        LocalDate date = LocalDate.now();

        LoanDisbursementResponse r1 = LoanDisbursementResponse.builder()
                .id(1L)
                .loanId(10L)
                .disbursedBy(100L)
                .amount(new BigDecimal("50000"))
                .disbursementDate(date)
                .createdAt(now)
                .build();

        LoanDisbursementResponse r2 = LoanDisbursementResponse.builder()
                .id(1L)
                .loanId(10L)
                .disbursedBy(100L)
                .amount(new BigDecimal("50000"))
                .disbursementDate(date)
                .createdAt(now)
                .build();

        LoanDisbursementResponse r3 = LoanDisbursementResponse.builder()
                .id(2L)
                .loanId(20L)
                .build();

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
        assertNotEquals(r1, r3);

        // Lombok canEqual path
        assertTrue(r1.canEqual(r2));
    }

    @Test
    void equals_shouldReturnFalseForNullAndDifferentType() {
        LoanDisbursementResponse response = LoanDisbursementResponse.builder()
                .id(1L)
                .build();

        assertNotEquals(response, null);
        assertNotEquals(response, "not-a-loan-disbursement-response");
    }

    @Test
    void toString_shouldContainClassNameAndKeyFields() {
        LoanDisbursementResponse response = LoanDisbursementResponse.builder()
                .id(5L)
                .referenceNumber("TXN-001")
                .disbursementMethod("BANK_TRANSFER")
                .build();

        String value = response.toString();

        assertNotNull(value);
        assertTrue(value.contains("LoanDisbursementResponse"));
        assertTrue(value.contains("TXN-001"));
        assertTrue(value.contains("BANK_TRANSFER"));
    }

    @Test
    void canEqual_shouldReturnFalseForDifferentClass() {
        LoanDisbursementResponse response = LoanDisbursementResponse.builder()
                .id(1L)
                .build();

        assertFalse(response.canEqual("invalid-object"));
    }

}
