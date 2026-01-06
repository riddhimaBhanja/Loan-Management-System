package com.loanmanagement.reporting.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Response DTO for payment statistics
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentStatisticsResponse {
    private Long totalPayments;
    private BigDecimal totalCollected;
    private BigDecimal averagePayment;
    private Map<String, BigDecimal> collectionByMethod; // NEFT: 100000, UPI: 50000, etc.
    private Integer onTimePayments;
    private Integer latePayments;
    private Integer overduePayments;
    private Double onTimePercentage;
    private BigDecimal totalPending;
    private BigDecimal totalOverdue;
}
