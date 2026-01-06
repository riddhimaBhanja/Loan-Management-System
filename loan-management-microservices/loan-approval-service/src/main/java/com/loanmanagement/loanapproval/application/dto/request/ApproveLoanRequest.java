package com.loanmanagement.loanapproval.application.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request DTO for approving a loan
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApproveLoanRequest {

    @NotNull(message = "Approved amount is required")
    @DecimalMin(value = "1000.00", message = "Approved amount must be at least 1000")
    @Digits(integer = 13, fraction = 2, message = "Invalid amount format")
    private BigDecimal approvedAmount;

    @NotNull(message = "Interest rate is required")
    @DecimalMin(value = "0.01", message = "Interest rate must be greater than 0")
    @DecimalMax(value = "50.00", message = "Interest rate cannot exceed 50%")
    @Digits(integer = 2, fraction = 2, message = "Invalid interest rate format")
    private BigDecimal interestRate;

    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    private String notes;
}
