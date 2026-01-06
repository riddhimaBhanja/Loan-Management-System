package com.loanmanagement.reporting.infrastructure.client;

import com.loanmanagement.common.dto.EmiScheduleDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Feign Client for EMI Service
 */
@FeignClient(name = "EMI-SERVICE")
public interface EmiServiceClient {

    @GetMapping("/api/internal/emis/statistics/total-collected")
    BigDecimal getTotalCollected();

    @GetMapping("/api/internal/emis/statistics/pending-amount")
    BigDecimal getPendingAmount();

    @GetMapping("/api/internal/emis/statistics/overdue")
    Map<String, Object> getOverdueStatistics();

    @GetMapping("/api/internal/emis/loan/{loanId}")
    List<EmiScheduleDTO> getEmisByLoanId(@PathVariable Long loanId);

    @GetMapping("/api/internal/emis/customer/{customerId}")
    List<EmiScheduleDTO> getEmisByCustomerId(@PathVariable Long customerId);

    @GetMapping("/api/internal/emis/statistics/payment")
    Map<String, Object> getPaymentStatistics(@RequestParam(required = false) String startDate,
                                             @RequestParam(required = false) String endDate);

    @GetMapping("/api/internal/emis/statistics/customer/{customerId}/summary")
    Map<String, Object> getCustomerEmiSummary(@PathVariable Long customerId);
}
