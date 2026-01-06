package com.loanmanagement.loanapproval.domain.service;

import com.loanmanagement.common.dto.LoanDTO;
import com.loanmanagement.common.dto.UserDetailsDTO;
import com.loanmanagement.common.exception.BusinessException;
import com.loanmanagement.common.exception.ResourceNotFoundException;
import com.loanmanagement.loanapproval.application.dto.request.DisburseLoanRequest;
import com.loanmanagement.loanapproval.application.dto.response.LoanDisbursementResponse;
import com.loanmanagement.loanapproval.application.mapper.LoanDisbursementMapper;
import com.loanmanagement.loanapproval.domain.model.LoanDisbursement;
import com.loanmanagement.loanapproval.domain.repository.LoanDisbursementRepository;
import com.loanmanagement.loanapproval.infrastructure.client.EmiServiceClient;
import com.loanmanagement.loanapproval.infrastructure.client.LoanApplicationServiceClient;
import com.loanmanagement.loanapproval.infrastructure.client.UserServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of LoanDisbursementService
 */
@Service
@Transactional
public class LoanDisbursementServiceImpl implements LoanDisbursementService {

    private static final Logger logger = LoggerFactory.getLogger(LoanDisbursementServiceImpl.class);

    private final LoanDisbursementRepository loanDisbursementRepository;
    private final LoanDisbursementMapper loanDisbursementMapper;
    private final LoanApplicationServiceClient loanApplicationServiceClient;
    private final UserServiceClient userServiceClient;
    private final EmiServiceClient emiServiceClient;
    private final com.loanmanagement.loanapproval.domain.repository.LoanApprovalRepository loanApprovalRepository;

    public LoanDisbursementServiceImpl(
            LoanDisbursementRepository loanDisbursementRepository,
            LoanDisbursementMapper loanDisbursementMapper,
            LoanApplicationServiceClient loanApplicationServiceClient,
            UserServiceClient userServiceClient,
            EmiServiceClient emiServiceClient,
            com.loanmanagement.loanapproval.domain.repository.LoanApprovalRepository loanApprovalRepository) {
        this.loanDisbursementRepository = loanDisbursementRepository;
        this.loanDisbursementMapper = loanDisbursementMapper;
        this.loanApplicationServiceClient = loanApplicationServiceClient;
        this.userServiceClient = userServiceClient;
        this.emiServiceClient = emiServiceClient;
        this.loanApprovalRepository = loanApprovalRepository;
    }

    @Override
    public LoanDisbursementResponse disburseLoan(Long loanId, DisburseLoanRequest request, Long disbursedBy) {
        logger.info("Disbursing loan ID: {} by user ID: {}", loanId, disbursedBy);

        // 1. Verify disburser has required role
        if (!userServiceClient.userHasAnyRole(disbursedBy, List.of("LOAN_OFFICER", "ADMIN"))) {
            throw new BusinessException("User does not have permission to disburse loans");
        }

        // 2. Get loan details from loan-application-service
        LoanDTO loan = loanApplicationServiceClient.getLoanById(loanId);

        // 3. Verify loan status is APPROVED
        if (!loan.getStatus().equals("APPROVED")) {
            throw new BusinessException("Only APPROVED loans can be disbursed. Current status: " + loan.getStatus());
        }

        // 4. Get approval details to retrieve approved amount and interest rate
        com.loanmanagement.loanapproval.domain.model.LoanApproval approval = loanApprovalRepository.findByLoanId(loanId)
                .orElseThrow(() -> new BusinessException("Loan approval record not found for loan ID: " + loanId));

        if (approval.getApprovedAmount() == null) {
            throw new BusinessException("Approved amount not found in approval record");
        }

        // 5. Check if loan already disbursed
        if (loanDisbursementRepository.existsByLoanId(loanId)) {
            throw new BusinessException("Loan has already been disbursed");
        }

        // 6. Validate disbursement date is not in future
        if (request.getDisbursementDate().isAfter(java.time.LocalDate.now())) {
            throw new BusinessException("Disbursement date cannot be in the future");
        }

        // 7. Create disbursement record
        LoanDisbursement disbursement = LoanDisbursement.builder()
                .loanId(loanId)
                .disbursedBy(disbursedBy)
                .amount(approval.getApprovedAmount())
                .disbursementDate(request.getDisbursementDate())
                .disbursementMethod(request.getDisbursementMethod())
                .referenceNumber(request.getReferenceNumber())
                .remarks(request.getRemarks())
                .build();

        LoanDisbursement savedDisbursement = loanDisbursementRepository.save(disbursement);

        // 8. Update loan status to DISBURSED in loan-application-service
        loanApplicationServiceClient.updateLoanStatusToDisbursed(
                loanId,
                request.getDisbursementDate(),
                request.getDisbursementMethod(),
                request.getReferenceNumber()
        );

        // 9. Generate EMI schedule via emi-service
        try {
            emiServiceClient.generateEmiSchedule(
                    loanId,
                    loan.getCustomerId(),
                    approval.getApprovedAmount(),
                    approval.getInterestRate(),
                    loan.getTenureMonths(),
                    request.getDisbursementDate()
            );
            logger.info("EMI schedule generation requested for loan {}", loanId);
        } catch (Exception e) {
            logger.error("Failed to generate EMI schedule for loan {}: {}", loanId, e.getMessage());
            // Don't fail disbursement if EMI generation fails - can be retried
        }

        logger.info("Loan {} disbursed successfully by user {}", loanId, disbursedBy);

        // 9. Build response with user details
        LoanDisbursementResponse response = loanDisbursementMapper.toResponse(savedDisbursement);
        enrichWithUserDetails(response, disbursedBy);

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public LoanDisbursementResponse getDisbursementByLoanId(Long loanId) {
        logger.info("Fetching disbursement details for loan ID: {}", loanId);

        LoanDisbursement disbursement = loanDisbursementRepository.findByLoanId(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Disbursement not found for loan ID: " + loanId));

        LoanDisbursementResponse response = loanDisbursementMapper.toResponse(disbursement);
        enrichWithUserDetails(response, disbursement.getDisbursedBy());

        return response;
    }

    /**
     * Enrich response with user details
     */
    private void enrichWithUserDetails(LoanDisbursementResponse response, Long userId) {
        try {
            UserDetailsDTO user = userServiceClient.getUserById(userId);
            response.setDisbursedByName(user.getFirstName() + " " + user.getLastName());
        } catch (Exception e) {
            logger.warn("Could not fetch user details for user ID: {}", userId, e);
            response.setDisbursedByName("Unknown");
        }
    }
}
