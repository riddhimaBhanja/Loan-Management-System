package com.loanmanagement.emi.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;

/**
 * Feign client for Loan Approval Service
 */
@FeignClient(name = "LOAN-APPROVAL-SERVICE", path = "/api/internal/loans")
public interface LoanApprovalServiceClient {

    /**
     * Get loan disbursement details
     *
     * @param loanId Loan ID
     * @return Disbursement details
     */
    @GetMapping("/{loanId}/disbursement")
    DisbursementDetailsDTO getDisbursementDetails(@PathVariable("loanId") Long loanId);

    /**
     * Check if loan is disbursed
     *
     * @param loanId Loan ID
     * @return true if disbursed
     */
    @GetMapping("/{loanId}/is-disbursed")
    boolean isLoanDisbursed(@PathVariable("loanId") Long loanId);

    /**
     * Notify that all EMIs are paid (for loan closure)
     *
     * @param loanId Loan ID
     */
    @GetMapping("/{loanId}/notify-emis-paid")
    void notifyAllEmisPaid(@PathVariable("loanId") Long loanId);

    /**
     * DTO for disbursement details
     */
    class DisbursementDetailsDTO {
        private Long loanId;
        private Long customerId;
        private BigDecimal disbursedAmount;
        private BigDecimal interestRate;
        private Integer tenureMonths;
        private java.time.LocalDate disbursementDate;

        // Getters and Setters
        public Long getLoanId() {
            return loanId;
        }

        public void setLoanId(Long loanId) {
            this.loanId = loanId;
        }

        public Long getCustomerId() {
            return customerId;
        }

        public void setCustomerId(Long customerId) {
            this.customerId = customerId;
        }

        public BigDecimal getDisbursedAmount() {
            return disbursedAmount;
        }

        public void setDisbursedAmount(BigDecimal disbursedAmount) {
            this.disbursedAmount = disbursedAmount;
        }

        public BigDecimal getInterestRate() {
            return interestRate;
        }

        public void setInterestRate(BigDecimal interestRate) {
            this.interestRate = interestRate;
        }

        public Integer getTenureMonths() {
            return tenureMonths;
        }

        public void setTenureMonths(Integer tenureMonths) {
            this.tenureMonths = tenureMonths;
        }

        public java.time.LocalDate getDisbursementDate() {
            return disbursementDate;
        }

        public void setDisbursementDate(java.time.LocalDate disbursementDate) {
            this.disbursementDate = disbursementDate;
        }
    }
}
