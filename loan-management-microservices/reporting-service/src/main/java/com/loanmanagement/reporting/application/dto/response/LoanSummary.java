package com.loanmanagement.reporting.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Summary DTO for loan information
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanSummary {
    private Long id;
    private String loanType;
    private BigDecimal requestedAmount;
    private BigDecimal approvedAmount;
    private String status;
    private String customerName;
    private String customerEmail;
    private LocalDateTime appliedAt;
    private LocalDate disbursementDate;
    private Integer tenureMonths;
    private BigDecimal interestRate;
}
