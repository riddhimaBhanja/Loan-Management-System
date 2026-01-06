package com.loanmanagement.emi.domain.service;

import com.loanmanagement.emi.application.dto.request.EmiPaymentRequest;
import com.loanmanagement.emi.application.dto.response.EmiPaymentResponse;

import java.util.List;

/**
 * Service interface for EMI Payment operations
 */
public interface EmiPaymentService {

    /**
     * Record EMI payment
     *
     * @param request Payment request
     * @return Payment response
     */
    EmiPaymentResponse recordPayment(EmiPaymentRequest request);

    /**
     * Get payment history for a loan
     *
     * @param loanId Loan ID
     * @return List of payments
     */
    List<EmiPaymentResponse> getPaymentHistory(Long loanId);

    /**
     * Get payment details by ID
     *
     * @param paymentId Payment ID
     * @return Payment response
     */
    EmiPaymentResponse getPaymentById(Long paymentId);

    /**
     * Get payment by transaction reference
     *
     * @param transactionReference Transaction reference
     * @return Payment response
     */
    EmiPaymentResponse getPaymentByTransactionReference(String transactionReference);
}
