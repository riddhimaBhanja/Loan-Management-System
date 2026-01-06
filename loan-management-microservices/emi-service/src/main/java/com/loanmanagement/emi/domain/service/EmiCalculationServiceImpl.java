package com.loanmanagement.emi.domain.service;

import com.loanmanagement.emi.domain.model.EmiSchedule;
import com.loanmanagement.emi.domain.model.EmiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of EMI Calculation Service
 * Uses reducing balance method for EMI calculation
 */
@Service
public class EmiCalculationServiceImpl implements EmiCalculationService {

    private static final Logger logger = LoggerFactory.getLogger(EmiCalculationServiceImpl.class);
    private static final int PRECISION = 10;
    private static final int SCALE = 2;

    @Override
    public BigDecimal calculateEmi(BigDecimal principal, BigDecimal annualRate, Integer tenureMonths) {
        logger.debug("Calculating EMI - Principal: {}, Rate: {}%, Tenure: {} months",
                principal, annualRate, tenureMonths);

        if (principal.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Principal must be greater than zero");
        }
        if (annualRate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Interest rate must be greater than zero");
        }
        if (tenureMonths <= 0) {
            throw new IllegalArgumentException("Tenure must be greater than zero");
        }

        // Convert annual rate to monthly decimal rate
        // R = (annualRate / 12) / 100
        BigDecimal monthlyRate = annualRate
                .divide(BigDecimal.valueOf(12), PRECISION, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(100), PRECISION, RoundingMode.HALF_UP);

        logger.debug("Monthly rate (decimal): {}", monthlyRate);

        // Calculate (1 + R)
        BigDecimal onePlusR = BigDecimal.ONE.add(monthlyRate);

        // Calculate (1 + R)^N
        BigDecimal power = onePlusR.pow(tenureMonths);

        logger.debug("(1+R)^N: {}", power);

        // Calculate numerator: P × R × (1+R)^N
        BigDecimal numerator = principal
                .multiply(monthlyRate)
                .multiply(power);

        // Calculate denominator: (1+R)^N - 1
        BigDecimal denominator = power.subtract(BigDecimal.ONE);

        // Calculate EMI
        BigDecimal emi = numerator.divide(denominator, SCALE, RoundingMode.HALF_UP);

        logger.info("EMI calculated: {} for principal: {}, rate: {}%, tenure: {} months",
                emi, principal, annualRate, tenureMonths);

        return emi;
    }

    @Override
    public BigDecimal calculateTotalInterest(BigDecimal emi, BigDecimal principal, Integer tenureMonths) {
        // Total Interest = (EMI × Tenure) - Principal
        BigDecimal totalPayable = emi.multiply(BigDecimal.valueOf(tenureMonths));
        BigDecimal totalInterest = totalPayable.subtract(principal);

        logger.debug("Total interest calculated: {}", totalInterest);

        return totalInterest.setScale(SCALE, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calculateTotalPayable(BigDecimal emi, Integer tenureMonths) {
        // Total Payable = EMI × Tenure
        BigDecimal totalPayable = emi.multiply(BigDecimal.valueOf(tenureMonths));

        logger.debug("Total payable calculated: {}", totalPayable);

        return totalPayable.setScale(SCALE, RoundingMode.HALF_UP);
    }

    @Override
    public List<EmiSchedule> generateEmiSchedule(
            Long loanId,
            Long customerId,
            BigDecimal principal,
            BigDecimal annualRate,
            Integer tenureMonths,
            LocalDate startDate) {

        logger.info("Generating EMI schedule for loan ID: {}, Amount: {}, Rate: {}%, Tenure: {} months",
                loanId, principal, annualRate, tenureMonths);

        List<EmiSchedule> scheduleList = new ArrayList<>();

        // Calculate monthly EMI
        BigDecimal emi = calculateEmi(principal, annualRate, tenureMonths);

        // Convert annual rate to monthly decimal rate
        BigDecimal monthlyRate = annualRate
                .divide(BigDecimal.valueOf(12), PRECISION, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(100), PRECISION, RoundingMode.HALF_UP);

        BigDecimal outstandingBalance = principal;
        LocalDate dueDate = startDate;

        for (int i = 1; i <= tenureMonths; i++) {
            // Calculate interest component for this month
            // Interest = Outstanding Balance × Monthly Rate
            BigDecimal interestAmount = outstandingBalance
                    .multiply(monthlyRate)
                    .setScale(SCALE, RoundingMode.HALF_UP);

            // Calculate principal component for this month
            // Principal = EMI - Interest
            BigDecimal principalAmount = emi.subtract(interestAmount);

            // For the last installment, adjust principal to clear remaining balance
            if (i == tenureMonths) {
                principalAmount = outstandingBalance;
                // Recalculate EMI for last installment to include any rounding differences
                emi = principalAmount.add(interestAmount);
            }

            // Update outstanding balance
            outstandingBalance = outstandingBalance.subtract(principalAmount);

            // Ensure outstanding balance doesn't go negative due to rounding
            if (outstandingBalance.compareTo(BigDecimal.ZERO) < 0) {
                outstandingBalance = BigDecimal.ZERO;
            }

            // Create EMI schedule entry
            EmiSchedule schedule = EmiSchedule.builder()
                    .loanId(loanId)
                    .customerId(customerId)
                    .emiNumber(i)
                    .dueDate(dueDate)
                    .principalComponent(principalAmount.setScale(SCALE, RoundingMode.HALF_UP))
                    .interestComponent(interestAmount)
                    .emiAmount(emi.setScale(SCALE, RoundingMode.HALF_UP))
                    .outstandingBalance(outstandingBalance.setScale(SCALE, RoundingMode.HALF_UP))
                    .status(EmiStatus.PENDING)
                    .build();

            scheduleList.add(schedule);

            // Move due date to next month
            dueDate = dueDate.plusMonths(1);

            logger.debug("EMI #{}: Principal={}, Interest={}, EMI={}, Outstanding={}",
                    i, principalAmount, interestAmount, emi, outstandingBalance);
        }

        logger.info("EMI schedule generated successfully with {} installments", scheduleList.size());

        return scheduleList;
    }
}
