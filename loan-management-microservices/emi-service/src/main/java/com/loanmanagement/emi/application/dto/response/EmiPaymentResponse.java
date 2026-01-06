package com.loanmanagement.emi.application.dto.response;

import com.loanmanagement.emi.domain.model.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Response DTO for EmiPayment entity
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmiPaymentResponse {

    private Long id;
    private Long emiScheduleId;
    private Long loanId;
    private Integer emiNumber;
    private BigDecimal amount;
    private LocalDate paymentDate;
    private PaymentMethod paymentMethod;
    private String transactionReference;
    private Long paidBy;
    private String paidByName;
    private String remarks;
    private LocalDateTime createdAt;
}
