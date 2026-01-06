package com.loanmanagement.common.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * EMI Schedule DTO for inter-service communication
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmiScheduleDTO {
    private Long id;
    private Long loanId;
    private Integer installmentNumber;
    private LocalDate dueDate;
    private BigDecimal principalAmount;
    private BigDecimal interestAmount;
    private BigDecimal totalEmi;
    private BigDecimal outstandingBalance;
    private String status;
}
