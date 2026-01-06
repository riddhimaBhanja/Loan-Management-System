package com.loanmanagement.emi.application.dto.request;

import com.loanmanagement.emi.domain.model.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request DTO for recording EMI payment
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmiPaymentRequest {

    @NotNull(message = "EMI schedule ID is required")
    private Long emiScheduleId;

    @NotNull(message = "Payment amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Payment amount must be greater than zero")
    private BigDecimal amount;

    @NotNull(message = "Payment date is required")
    private LocalDate paymentDate;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    @Size(max = 100, message = "Transaction reference must not exceed 100 characters")
    private String transactionReference;

    @Size(max = 500, message = "Remarks must not exceed 500 characters")
    private String remarks;
}
