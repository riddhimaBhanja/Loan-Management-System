package com.loanmanagement.loanapp.application.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request DTO for creating a new loan type
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateLoanTypeRequest {

    @NotBlank(message = "Loan type name is required")
    @Size(max = 50, message = "Name must not exceed 50 characters")
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @NotNull(message = "Minimum amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Minimum amount must be greater than zero")
    private BigDecimal minAmount;

    @NotNull(message = "Maximum amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Maximum amount must be greater than zero")
    private BigDecimal maxAmount;

    @NotNull(message = "Minimum tenure is required")
    @Min(value = 1, message = "Minimum tenure must be at least 1 month")
    private Integer minTenureMonths;

    @NotNull(message = "Maximum tenure is required")
    @Min(value = 1, message = "Maximum tenure must be at least 1 month")
    private Integer maxTenureMonths;

    @NotNull(message = "Interest rate is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Interest rate must be greater than zero")
    @DecimalMax(value = "100.0", message = "Interest rate must not exceed 100%")
    private BigDecimal interestRate;

    @DecimalMin(value = "0.0", message = "Late fee percentage must be non-negative")
    @DecimalMax(value = "100.0", message = "Late fee percentage must not exceed 100%")
    @Builder.Default
    private BigDecimal lateFeePercentage = BigDecimal.valueOf(2.0);

    @Min(value = 0, message = "Grace period days must be non-negative")
    @Builder.Default
    private Integer gracePeriodDays = 3;

    @Builder.Default
    private Boolean isActive = true;
}
