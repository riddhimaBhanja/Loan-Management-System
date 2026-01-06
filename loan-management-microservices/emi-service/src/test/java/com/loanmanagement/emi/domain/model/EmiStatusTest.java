package com.loanmanagement.emi.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmiStatusTest {

    @Test
    void getDisplayName_shouldReturnCorrectDisplayName() {
        assertEquals("Pending", EmiStatus.PENDING.getDisplayName());
        assertEquals("Paid", EmiStatus.PAID.getDisplayName());
        assertEquals("Overdue", EmiStatus.OVERDUE.getDisplayName());
        assertEquals("Partial Paid", EmiStatus.PARTIAL_PAID.getDisplayName());
    }

    @Test
    void canMarkAsPaid_shouldReturnTrueForAllowedStatuses() {
        assertTrue(EmiStatus.PENDING.canMarkAsPaid());
        assertTrue(EmiStatus.OVERDUE.canMarkAsPaid());
        assertTrue(EmiStatus.PARTIAL_PAID.canMarkAsPaid());
    }

    @Test
    void canMarkAsPaid_shouldReturnFalseForPaidStatus() {
        assertFalse(EmiStatus.PAID.canMarkAsPaid());
    }

    @Test
    void isUnpaid_shouldReturnTrueForUnpaidStatuses() {
        assertTrue(EmiStatus.PENDING.isUnpaid());
        assertTrue(EmiStatus.OVERDUE.isUnpaid());
        assertTrue(EmiStatus.PARTIAL_PAID.isUnpaid());
    }

    @Test
    void isUnpaid_shouldReturnFalseForPaidStatus() {
        assertFalse(EmiStatus.PAID.isUnpaid());
    }
}
