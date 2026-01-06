package com.loanmanagement.emi.domain.service;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Service interface for late fee calculation
 */
public interface LateFeeCalculationService {

    /**
     * Calculate late fee for an overdue EMI
     *
     * @param emiAmount EMI amount
     * @param dueDate Due date of EMI
     * @param paymentDate Actual payment date
     * @param lateFeePercentage Late fee percentage per day
     * @param gracePeriodDays Grace period before late fee applies
     * @return Calculated late fee amount
     */
    BigDecimal calculateLateFee(
        BigDecimal emiAmount,
        LocalDate dueDate,
        LocalDate paymentDate,
        BigDecimal lateFeePercentage,
        Integer gracePeriodDays
    );

    /**
     * Get number of days late (after grace period)
     *
     * @param dueDate Due date
     * @param paymentDate Payment date
     * @param gracePeriodDays Grace period
     * @return Number of chargeable late days
     */
    long getChargeableLateDays(LocalDate dueDate, LocalDate paymentDate, Integer gracePeriodDays);

    /**
     * Check if payment is late (beyond grace period)
     *
     * @param dueDate Due date
     * @param paymentDate Payment date
     * @param gracePeriodDays Grace period
     * @return true if late fee applicable
     */
    boolean isPaymentLate(LocalDate dueDate, LocalDate paymentDate, Integer gracePeriodDays);
}
