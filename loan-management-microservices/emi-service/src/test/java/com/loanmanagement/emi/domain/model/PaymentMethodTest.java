package com.loanmanagement.emi.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaymentMethodTest {

    @Test
    void getDisplayName_shouldReturnCorrectDisplayName() {
        assertEquals("Cash", PaymentMethod.CASH.getDisplayName());
        assertEquals("Cheque", PaymentMethod.CHEQUE.getDisplayName());
        assertEquals("NEFT", PaymentMethod.NEFT.getDisplayName());
        assertEquals("RTGS", PaymentMethod.RTGS.getDisplayName());
        assertEquals("UPI", PaymentMethod.UPI.getDisplayName());
        assertEquals("Debit Card", PaymentMethod.DEBIT_CARD.getDisplayName());
        assertEquals("Credit Card", PaymentMethod.CREDIT_CARD.getDisplayName());
        assertEquals("Net Banking", PaymentMethod.NET_BANKING.getDisplayName());
        assertEquals("Demand Draft", PaymentMethod.DEMAND_DRAFT.getDisplayName());
    }

    @Test
    void requiresTransactionReference_shouldReturnFalseForCash() {
        assertFalse(PaymentMethod.CASH.requiresTransactionReference());
    }

    @Test
    void requiresTransactionReference_shouldReturnTrueForNonCashMethods() {
        assertTrue(PaymentMethod.CHEQUE.requiresTransactionReference());
        assertTrue(PaymentMethod.NEFT.requiresTransactionReference());
        assertTrue(PaymentMethod.RTGS.requiresTransactionReference());
        assertTrue(PaymentMethod.UPI.requiresTransactionReference());
        assertTrue(PaymentMethod.DEBIT_CARD.requiresTransactionReference());
        assertTrue(PaymentMethod.CREDIT_CARD.requiresTransactionReference());
        assertTrue(PaymentMethod.NET_BANKING.requiresTransactionReference());
        assertTrue(PaymentMethod.DEMAND_DRAFT.requiresTransactionReference());
    }
}
