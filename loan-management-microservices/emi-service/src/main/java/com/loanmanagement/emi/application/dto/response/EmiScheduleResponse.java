package com.loanmanagement.emi.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.loanmanagement.emi.domain.model.EmiStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Response DTO for EmiSchedule entity
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmiScheduleResponse {

    private Long id;
    private Long loanId;
    private Long customerId;
    private Integer emiNumber;
    private BigDecimal emiAmount;
    private BigDecimal principalComponent;
    private BigDecimal interestComponent;
    private LocalDate dueDate;
    private BigDecimal outstandingBalance;
    private EmiStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
