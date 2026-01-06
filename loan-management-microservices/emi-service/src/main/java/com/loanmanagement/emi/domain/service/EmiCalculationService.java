package com.loanmanagement.emi.domain.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for EMI calculation operations
 */
public interface EmiCalculationService {

    /**
     * Calculate monthly EMI amount using reducing balance method
     *
     * Formula: EMI = [P × R × (1+R)^N] / [(1+R)^N - 1]
     * Where:
     * P = Principal loan amount
     * R = Monthly interest rate (annual rate / 12 / 100)
     * N = Tenure in months
     *
     * @param principal      Loan principal amount
     * @param annualRate     Annual interest rate (e.g., 12.5 for 12.5%)
     * @param tenureMonths   Loan tenure in months
     * @return Monthly EMI amount
     */
    BigDecimal calculateEmi(BigDecimal principal, BigDecimal annualRate, Integer tenureMonths);

    /**
     * Calculate total interest payable
     *
     * @param emi           Monthly EMI amount
     * @param principal     Loan principal amount
     * @param tenureMonths  Loan tenure in months
     * @return Total interest amount
     */
    BigDecimal calculateTotalInterest(BigDecimal emi, BigDecimal principal, Integer tenureMonths);

    /**
     * Calculate total amount payable
     *
     * @param emi           Monthly EMI amount
     * @param tenureMonths  Loan tenure in months
     * @return Total payable amount
     */
    BigDecimal calculateTotalPayable(BigDecimal emi, Integer tenureMonths);

    /**
     * Generate EMI schedule breakdown for a loan
     *
     * @param loanId         Loan ID
     * @param customerId     Customer ID
     * @param principal      Loan principal amount
     * @param annualRate     Annual interest rate
     * @param tenureMonths   Loan tenure in months
     * @param startDate      First EMI due date
     * @return List of EMI schedules with principal and interest breakdown
     */
    List<com.loanmanagement.emi.domain.model.EmiSchedule> generateEmiSchedule(
            Long loanId,
            Long customerId,
            BigDecimal principal,
            BigDecimal annualRate,
            Integer tenureMonths,
            LocalDate startDate
    );
}
