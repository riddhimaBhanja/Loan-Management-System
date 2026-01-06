package com.loanmanagement.loanapp.domain.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoanStatusTest {

    @Test
    void valueOf_shouldReturnCorrectEnum() {
        LoanStatus status = LoanStatus.valueOf("PENDING");
        assertEquals(LoanStatus.PENDING, status);
    }

    @Test
    void getDisplayName_shouldReturnCorrectValue() {
        assertEquals("Pending", LoanStatus.PENDING.getDisplayName());
        assertEquals("Approved", LoanStatus.APPROVED.getDisplayName());
        assertEquals("Rejected", LoanStatus.REJECTED.getDisplayName());
        assertEquals("Disbursed", LoanStatus.DISBURSED.getDisplayName());
        assertEquals("Closed", LoanStatus.CLOSED.getDisplayName());
    }

    @Test
    void values_shouldContainAllStatuses() {
        LoanStatus[] values = LoanStatus.values();

        assertEquals(5, values.length);
        assertArrayEquals(
                new LoanStatus[]{
                        LoanStatus.PENDING,
                        LoanStatus.APPROVED,
                        LoanStatus.REJECTED,
                        LoanStatus.DISBURSED,
                        LoanStatus.CLOSED
                },
                values
        );
    }
}
