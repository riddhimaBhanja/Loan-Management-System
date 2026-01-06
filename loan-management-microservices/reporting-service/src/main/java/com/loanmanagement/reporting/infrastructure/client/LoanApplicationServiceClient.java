package com.loanmanagement.reporting.infrastructure.client;

import com.loanmanagement.common.dto.LoanDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * Feign Client for Loan Application Service
 */
@FeignClient(name = "LOAN-APPLICATION-SERVICE")
public interface LoanApplicationServiceClient {

    @GetMapping("/api/internal/loans/count")
    Long getTotalLoans();

    @GetMapping("/api/internal/loans/status/{status}/count")
    Long getLoansByStatus(@PathVariable String status);

    @GetMapping("/api/internal/loans/recent")
    List<LoanDTO> getRecentLoans(@RequestParam(defaultValue = "10") int limit);

    @GetMapping("/api/internal/loans/customer/{customerId}")
    List<LoanDTO> getLoansByCustomerId(@PathVariable Long customerId);

    @GetMapping("/api/internal/loans")
    List<LoanDTO> getAllLoans();

    @GetMapping("/api/internal/loans/statistics")
    Map<String, Object> getLoanStatistics(@RequestParam(required = false) String startDate,
                                          @RequestParam(required = false) String endDate);

    @GetMapping("/api/internal/loans/officer/{officerId}")
    List<LoanDTO> getLoansByOfficerId(@PathVariable Long officerId);
}
