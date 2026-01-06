package com.loanmanagement.common.dto;

import lombok.*;

import java.math.BigDecimal;

/**
 * Loan Type DTO for inter-service communication
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanTypeDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private Integer minTenureMonths;
    private Integer maxTenureMonths;
    private BigDecimal interestRate;
    private BigDecimal lateFeePercentage;
    private Integer gracePeriodDays;
    private Boolean isActive;
}
