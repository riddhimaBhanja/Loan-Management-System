package com.loanmanagement.emi.application.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request DTO for generating EMI schedule
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenerateEmiRequest {

    @NotNull(message = "Loan ID is required")
    private Long loanId;

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotNull(message = "Principal amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Principal must be greater than zero")
    private BigDecimal principal;

    @NotNull(message = "Interest rate is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Interest rate must be greater than zero")
    @DecimalMax(value = "100.0", message = "Interest rate must not exceed 100%")
    private BigDecimal interestRate;

    @NotNull(message = "Tenure is required")
    @Min(value = 1, message = "Tenure must be at least 1 month")
    @Max(value = 600, message = "Tenure must not exceed 600 months")
    private Integer tenureMonths;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;
}
