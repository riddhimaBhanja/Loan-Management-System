package com.loanmanagement.emi.domain.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Implementation of LateFeeCalculationService
 *
 * Calculates late fees based on:
 * - Number of days late (after grace period)
 * - Late fee percentage per day
 * - Original EMI amount
 *
 * Formula: Late Fee = EMI Amount × Late Fee Percentage × Days Late / 100
 */
@Service
public class LateFeeCalculationServiceImpl implements LateFeeCalculationService {

    private static final Logger logger = LoggerFactory.getLogger(LateFeeCalculationServiceImpl.class);

    /**
     * Calculate late fee for overdue payment
     *
     * @param emiAmount Original EMI amount
     * @param dueDate Due date of EMI
     * @param paymentDate Actual payment date
     * @param lateFeePercentage Late fee percentage per day (e.g., 2.0 for 2%)
     * @param gracePeriodDays Grace period before late fee applies (e.g., 3 days)
     * @return Calculated late fee amount
     */
    @Override
    public BigDecimal calculateLateFee(
            BigDecimal emiAmount,
            LocalDate dueDate,
            LocalDate paymentDate,
            BigDecimal lateFeePercentage,
            Integer gracePeriodDays) {

        // Validation
        if (emiAmount == null || dueDate == null || paymentDate == null) {
            logger.warn("Null parameters provided for late fee calculation");
            return BigDecimal.ZERO;
        }

        if (lateFeePercentage == null || lateFeePercentage.compareTo(BigDecimal.ZERO) <= 0) {
            logger.debug("No late fee percentage configured");
            return BigDecimal.ZERO;
        }

        // Check if payment is late
        if (!isPaymentLate(dueDate, paymentDate, gracePeriodDays)) {
            logger.debug("Payment is within grace period, no late fee applicable");
            return BigDecimal.ZERO;
        }

        // Calculate chargeable late days
        long lateDays = getChargeableLateDays(dueDate, paymentDate, gracePeriodDays);

        if (lateDays <= 0) {
            return BigDecimal.ZERO;
        }

        // Calculate late fee
        // Formula: (EMI Amount × Late Fee Percentage × Late Days) / 100
        BigDecimal lateFee = emiAmount
                .multiply(lateFeePercentage)
                .multiply(BigDecimal.valueOf(lateDays))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        logger.info("Late fee calculated: {} for {} days late on EMI amount: {}",
                lateFee, lateDays, emiAmount);

        return lateFee;
    }

    /**
     * Get number of chargeable late days (after grace period)
     *
     * @param dueDate Due date
     * @param paymentDate Payment date
     * @param gracePeriodDays Grace period
     * @return Number of late days to charge
     */
    @Override
    public long getChargeableLateDays(LocalDate dueDate, LocalDate paymentDate, Integer gracePeriodDays) {
        if (dueDate == null || paymentDate == null) {
            return 0;
        }

        // If paid on or before due date, no late days
        if (!paymentDate.isAfter(dueDate)) {
            return 0;
        }

        // Calculate total days late
        long totalDaysLate = ChronoUnit.DAYS.between(dueDate, paymentDate);

        // Set default grace period if not provided
        int grace = (gracePeriodDays != null && gracePeriodDays > 0) ? gracePeriodDays : 0;

        // Calculate chargeable days (total days - grace period)
        long chargeableDays = totalDaysLate - grace;

        return Math.max(0, chargeableDays);
    }

    /**
     * Check if payment is late (beyond grace period)
     *
     * @param dueDate Due date
     * @param paymentDate Payment date
     * @param gracePeriodDays Grace period
     * @return true if late fee is applicable
     */
    @Override
    public boolean isPaymentLate(LocalDate dueDate, LocalDate paymentDate, Integer gracePeriodDays) {
        if (dueDate == null || paymentDate == null) {
            return false;
        }

        // If paid on or before due date, not late
        if (!paymentDate.isAfter(dueDate)) {
            return false;
        }

        // Set default grace period
        int grace = (gracePeriodDays != null && gracePeriodDays > 0) ? gracePeriodDays : 0;

        // Calculate grace end date
        LocalDate graceEndDate = dueDate.plusDays(grace);

        // Payment is late if after grace end date
        return paymentDate.isAfter(graceEndDate);
    }
}
