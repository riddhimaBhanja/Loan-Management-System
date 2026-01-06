package com.loanmanagement.loanapp.application.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request DTO for updating an existing loan type
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateLoanTypeRequest {

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @DecimalMin(value = "0.0", inclusive = false, message = "Minimum amount must be greater than zero")
    private BigDecimal minAmount;

    @DecimalMin(value = "0.0", inclusive = false, message = "Maximum amount must be greater than zero")
    private BigDecimal maxAmount;

    @Min(value = 1, message = "Minimum tenure must be at least 1 month")
    private Integer minTenureMonths;

    @Min(value = 1, message = "Maximum tenure must be at least 1 month")
    private Integer maxTenureMonths;

    @DecimalMin(value = "0.0", inclusive = false, message = "Interest rate must be greater than zero")
    @DecimalMax(value = "100.0", message = "Interest rate must not exceed 100%")
    private BigDecimal interestRate;

    @DecimalMin(value = "0.0", message = "Late fee percentage must be non-negative")
    @DecimalMax(value = "100.0", message = "Late fee percentage must not exceed 100%")
    private BigDecimal lateFeePercentage;

    @Min(value = 0, message = "Grace period days must be non-negative")
    private Integer gracePeriodDays;

    private Boolean isActive;
}
