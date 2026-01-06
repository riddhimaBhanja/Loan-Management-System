package com.loanmanagement.emi.domain.service;

import com.loanmanagement.common.exception.BusinessException;
import com.loanmanagement.common.exception.ResourceNotFoundException;
import com.loanmanagement.emi.application.dto.request.EmiPaymentRequest;
import com.loanmanagement.emi.application.dto.response.EmiPaymentResponse;
import com.loanmanagement.emi.application.mapper.EmiPaymentMapper;
import com.loanmanagement.emi.domain.model.EmiPayment;
import com.loanmanagement.emi.domain.model.EmiSchedule;
import com.loanmanagement.emi.domain.repository.EmiPaymentRepository;
import com.loanmanagement.emi.domain.repository.EmiScheduleRepository;
import com.loanmanagement.emi.infrastructure.client.UserServiceClient;
import com.loanmanagement.emi.shared.constants.MessageConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Implementation of EmiPaymentService
 */
@Service
@Transactional
public class EmiPaymentServiceImpl implements EmiPaymentService {

    private static final Logger logger = LoggerFactory.getLogger(EmiPaymentServiceImpl.class);

    @Autowired
    private EmiPaymentRepository emiPaymentRepository;

    @Autowired
    private EmiScheduleRepository emiScheduleRepository;

    @Autowired
    private EmiPaymentMapper emiPaymentMapper;

    @Autowired
    private UserServiceClient userServiceClient;

    @Override
    public EmiPaymentResponse recordPayment(EmiPaymentRequest request) {
        logger.info("Recording payment for EMI schedule ID: {}", request.getEmiScheduleId());

        // Validate EMI schedule exists
        EmiSchedule emiSchedule = emiScheduleRepository.findById(request.getEmiScheduleId())
                .orElseThrow(() -> new ResourceNotFoundException(MessageConstants.EMI_NOT_FOUND));

        // Check if already paid
        if (emiSchedule.getStatus() == com.loanmanagement.emi.domain.model.EmiStatus.PAID) {
            throw new BusinessException(MessageConstants.EMI_ALREADY_PAID);
        }

        // Validate payment amount
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(MessageConstants.INVALID_PAYMENT_AMOUNT);
        }

        // Validate transaction reference for non-cash payments
        if (request.getPaymentMethod().requiresTransactionReference() &&
                (request.getTransactionReference() == null || request.getTransactionReference().isBlank())) {
            throw new BusinessException(MessageConstants.TRANSACTION_REFERENCE_REQUIRED);
        }

        // Check for duplicate transaction reference
        if (request.getTransactionReference() != null && !request.getTransactionReference().isBlank()) {
            emiPaymentRepository.findByTransactionReference(request.getTransactionReference())
                    .ifPresent(p -> {
                        throw new BusinessException(MessageConstants.DUPLICATE_TRANSACTION_REFERENCE);
                    });
        }

        // Get current user ID from security context
        Long currentUserId = getCurrentUserId();

        // Create payment record
        EmiPayment payment = EmiPayment.builder()
                .emiScheduleId(emiSchedule.getId())
                .loanId(emiSchedule.getLoanId())
                .amount(request.getAmount())
                .paymentDate(request.getPaymentDate())
                .paymentMethod(request.getPaymentMethod())
                .transactionReference(request.getTransactionReference())
                .paidBy(currentUserId)
                .remarks(request.getRemarks())
                .build();

        // Save payment
        EmiPayment savedPayment = emiPaymentRepository.save(payment);

        // Update EMI schedule status
        if (request.getAmount().compareTo(emiSchedule.getEmiAmount()) >= 0) {
            // Full payment
            emiSchedule.markAsPaid();
            logger.info("EMI #{} for loan {} marked as PAID", emiSchedule.getEmiNumber(), emiSchedule.getLoanId());
        } else {
            // Partial payment
            emiSchedule.markAsPartialPaid();
            logger.info("EMI #{} for loan {} marked as PARTIAL_PAID", emiSchedule.getEmiNumber(), emiSchedule.getLoanId());
        }

        emiScheduleRepository.save(emiSchedule);

        logger.info("Payment recorded successfully for loan: {}, EMI #{}",
                emiSchedule.getLoanId(), emiSchedule.getEmiNumber());

        // Map to response
        EmiPaymentResponse response = emiPaymentMapper.toResponse(savedPayment);
        response.setEmiNumber(emiSchedule.getEmiNumber());

        // Fetch user name (optional - handled gracefully if service is down)
        try {
            String userName = userServiceClient.getUserName(currentUserId);
            response.setPaidByName(userName);
        } catch (Exception e) {
            logger.warn("Failed to fetch user name for user ID: {}", currentUserId, e);
            response.setPaidByName("User #" + currentUserId);
        }

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmiPaymentResponse> getPaymentHistory(Long loanId) {
        logger.info("Fetching payment history for loan ID: {}", loanId);

        List<EmiPayment> payments = emiPaymentRepository.findByLoanIdOrderByPaymentDateDesc(loanId);

        List<EmiPaymentResponse> responses = emiPaymentMapper.toResponseList(payments);

        // Enrich with EMI numbers
        for (EmiPaymentResponse response : responses) {
            emiScheduleRepository.findById(response.getEmiScheduleId())
                    .ifPresent(schedule -> response.setEmiNumber(schedule.getEmiNumber()));
        }

        return responses;
    }

    @Override
    @Transactional(readOnly = true)
    public EmiPaymentResponse getPaymentById(Long paymentId) {
        logger.info("Fetching payment by ID: {}", paymentId);

        EmiPayment payment = emiPaymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageConstants.PAYMENT_NOT_FOUND));

        EmiPaymentResponse response = emiPaymentMapper.toResponse(payment);

        // Enrich with EMI number
        emiScheduleRepository.findById(payment.getEmiScheduleId())
                .ifPresent(schedule -> response.setEmiNumber(schedule.getEmiNumber()));

        // Fetch user name
        try {
            String userName = userServiceClient.getUserName(payment.getPaidBy());
            response.setPaidByName(userName);
        } catch (Exception e) {
            logger.warn("Failed to fetch user name for user ID: {}", payment.getPaidBy(), e);
            response.setPaidByName("User #" + payment.getPaidBy());
        }

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public EmiPaymentResponse getPaymentByTransactionReference(String transactionReference) {
        logger.info("Fetching payment by transaction reference: {}", transactionReference);

        EmiPayment payment = emiPaymentRepository.findByTransactionReference(transactionReference)
                .orElseThrow(() -> new ResourceNotFoundException(MessageConstants.PAYMENT_NOT_FOUND));

        EmiPaymentResponse response = emiPaymentMapper.toResponse(payment);

        // Enrich with EMI number
        emiScheduleRepository.findById(payment.getEmiScheduleId())
                .ifPresent(schedule -> response.setEmiNumber(schedule.getEmiNumber()));

        return response;
    }

    /**
     * Get current user ID from security context
     */
    private Long getCurrentUserId() {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
                String username = ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
                // Extract user ID from username (assuming username format or use UserServiceClient)
                return userServiceClient.getUserIdByUsername(username);
            }
            return 1L; // Default fallback for internal/system operations
        } catch (Exception e) {
            logger.warn("Failed to get current user ID, using default", e);
            return 1L; // Default fallback
        }
    }
}
