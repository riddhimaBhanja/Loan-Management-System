package com.loanmanagement.reporting.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign Client for Loan Approval Service
 */
@FeignClient(name = "LOAN-APPROVAL-SERVICE")
public interface LoanApprovalServiceClient {

    @GetMapping("/api/internal/approvals/approved/count")
    Long getApprovedLoansCount();

    @GetMapping("/api/internal/approvals/rejected/count")
    Long getRejectedLoansCount();

    @GetMapping("/api/internal/approvals/pending/count")
    Long getPendingApprovalsCount();

    @GetMapping("/api/internal/approvals/officer/{officerId}/pending/count")
    Long getPendingApprovalsByOfficerId(@PathVariable Long officerId);
}
