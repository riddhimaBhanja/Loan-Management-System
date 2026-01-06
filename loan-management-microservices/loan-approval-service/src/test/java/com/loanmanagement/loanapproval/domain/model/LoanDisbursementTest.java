package com.loanmanagement.loanapproval.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class LoanDisbursementTest {

    @Test
    void builderAndGetters_shouldWorkCorrectly() {
        LocalDateTime now = LocalDateTime.now();
        LocalDate disbursementDate = LocalDate.now();

        LoanDisbursement disbursement = LoanDisbursement.builder()
                .id(1L)
                .loanId(10L)
                .disbursedBy(100L)
                .amount(new BigDecimal("50000.00"))
                .disbursementDate(disbursementDate)
                .disbursementMethod("BANK_TRANSFER")
                .referenceNumber("TXN123456")
                .remarks("Disbursed successfully")
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertEquals(1L, disbursement.getId());
        assertEquals(10L, disbursement.getLoanId());
        assertEquals(100L, disbursement.getDisbursedBy());
        assertEquals(new BigDecimal("50000.00"), disbursement.getAmount());
        assertEquals(disbursementDate, disbursement.getDisbursementDate());
        assertEquals("BANK_TRANSFER", disbursement.getDisbursementMethod());
        assertEquals("TXN123456", disbursement.getReferenceNumber());
        assertEquals("Disbursed successfully", disbursement.getRemarks());
        assertEquals(now, disbursement.getCreatedAt());
        assertEquals(now, disbursement.getUpdatedAt());
    }

    @Test
    void noArgsConstructorAndSetters_shouldWorkCorrectly() {
        LocalDateTime now = LocalDateTime.now();
        LocalDate disbursementDate = LocalDate.now();

        LoanDisbursement disbursement = new LoanDisbursement();
        disbursement.setId(2L);
        disbursement.setLoanId(20L);
        disbursement.setDisbursedBy(200L);
        disbursement.setAmount(new BigDecimal("75000.00"));
        disbursement.setDisbursementDate(disbursementDate);
        disbursement.setDisbursementMethod("CHEQUE");
        disbursement.setReferenceNumber("REF987654");
        disbursement.setRemarks("Manual disbursement");
        disbursement.setCreatedAt(now);
        disbursement.setUpdatedAt(now);

        assertEquals(2L, disbursement.getId());
        assertEquals(20L, disbursement.getLoanId());
        assertEquals(200L, disbursement.getDisbursedBy());
        assertEquals(new BigDecimal("75000.00"), disbursement.getAmount());
        assertEquals(disbursementDate, disbursement.getDisbursementDate());
        assertEquals("CHEQUE", disbursement.getDisbursementMethod());
        assertEquals("REF987654", disbursement.getReferenceNumber());
        assertEquals("Manual disbursement", disbursement.getRemarks());
        assertEquals(now, disbursement.getCreatedAt());
        assertEquals(now, disbursement.getUpdatedAt());
    }

    @Test
    void prePersist_shouldInitializeDatesWhenNull() {
        LoanDisbursement disbursement = LoanDisbursement.builder()
                .loanId(30L)
                .disbursedBy(300L)
                .amount(new BigDecimal("90000.00"))
                .disbursementDate(LocalDate.now())
                .build();

        disbursement.onCreate();

        assertNotNull(disbursement.getCreatedAt());
        assertNotNull(disbursement.getUpdatedAt());
    }

    @Test
    void preUpdate_shouldUpdateUpdatedAt() throws InterruptedException {
        LoanDisbursement disbursement = new LoanDisbursement();
        LocalDateTime initialTime = LocalDateTime.now();
        disbursement.setUpdatedAt(initialTime);

        Thread.sleep(5);
        disbursement.onUpdate();

        assertTrue(disbursement.getUpdatedAt().isAfter(initialTime));
    }
}
