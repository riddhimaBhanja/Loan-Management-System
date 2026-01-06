package com.loanmanagement.loanapp.application.dto.request;

import com.loanmanagement.loanapp.domain.enums.EmploymentStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request DTO for loan application submission
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanApplicationRequest {

    @NotNull(message = "Loan type ID is required")
    private Long loanTypeId;

    @NotNull(message = "Requested amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Requested amount must be greater than zero")
    private BigDecimal amount;

    @NotNull(message = "Tenure is required")
    @Min(value = 1, message = "Tenure must be at least 1 month")
    @Max(value = 360, message = "Tenure must not exceed 360 months")
    private Integer tenureMonths;

    @NotNull(message = "Employment status is required")
    private EmploymentStatus employmentStatus;

    @NotNull(message = "Monthly income is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Monthly income must be greater than zero")
    private BigDecimal monthlyIncome;

    @Size(max = 1000, message = "Purpose must not exceed 1000 characters")
    private String purpose;
}
