package com.loanmanagement.common.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request DTO for generating EMI schedule
 * Used by Loan Approval Service to request EMI Service to generate schedule
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenerateEmiRequest {
    @NotNull(message = "Loan ID is required")
    private Long loanId;

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotNull(message = "Principal amount is required")
    @DecimalMin(value = "0.01", message = "Principal amount must be greater than 0")
    private BigDecimal principal;

    @NotNull(message = "Interest rate is required")
    @DecimalMin(value = "0.01", message = "Interest rate must be greater than 0")
    @DecimalMax(value = "100.00", message = "Interest rate cannot exceed 100%")
    private BigDecimal interestRate;

    @NotNull(message = "Tenure is required")
    @Min(value = 1, message = "Tenure must be at least 1 month")
    @Max(value = 600, message = "Tenure cannot exceed 600 months")
    private Integer tenureMonths;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;
}
