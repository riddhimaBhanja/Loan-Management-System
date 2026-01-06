package com.loanmanagement.emi.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Response DTO for EMI summary information
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmiSummaryResponse {

    private Long loanId;
    private Long customerId;
    private Integer totalEmis;
    private Integer paidEmis;
    private Integer pendingEmis;
    private Integer overdueEmis;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private BigDecimal pendingAmount;
    private BigDecimal outstandingAmount;
}
