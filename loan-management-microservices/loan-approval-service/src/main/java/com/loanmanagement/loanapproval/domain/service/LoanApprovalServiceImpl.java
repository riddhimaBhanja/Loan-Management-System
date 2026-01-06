package com.loanmanagement.loanapproval.domain.service;

import com.loanmanagement.common.dto.LoanDTO;
import com.loanmanagement.common.dto.UserDetailsDTO;
import com.loanmanagement.common.exception.BusinessException;
import com.loanmanagement.common.exception.ResourceNotFoundException;
import com.loanmanagement.loanapproval.application.dto.request.ApproveLoanRequest;
import com.loanmanagement.loanapproval.application.dto.request.RejectLoanRequest;
import com.loanmanagement.loanapproval.application.dto.response.LoanApprovalResponse;
import com.loanmanagement.loanapproval.application.mapper.LoanApprovalMapper;
import com.loanmanagement.loanapproval.domain.model.LoanApproval;
import com.loanmanagement.loanapproval.domain.repository.LoanApprovalRepository;
import com.loanmanagement.loanapproval.infrastructure.client.LoanApplicationServiceClient;
import com.loanmanagement.loanapproval.infrastructure.client.NotificationServiceClient;
import com.loanmanagement.loanapproval.infrastructure.client.UserServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementation of LoanApprovalService
 */
@Service
@Transactional
public class LoanApprovalServiceImpl implements LoanApprovalService {

    private static final Logger logger = LoggerFactory.getLogger(LoanApprovalServiceImpl.class);

    private final LoanApprovalRepository loanApprovalRepository;
    private final LoanApprovalMapper loanApprovalMapper;
    private final LoanApplicationServiceClient loanApplicationServiceClient;
    private final UserServiceClient userServiceClient;
    private final NotificationServiceClient notificationServiceClient;

    public LoanApprovalServiceImpl(
            LoanApprovalRepository loanApprovalRepository,
            LoanApprovalMapper loanApprovalMapper,
            LoanApplicationServiceClient loanApplicationServiceClient,
            UserServiceClient userServiceClient,
            NotificationServiceClient notificationServiceClient) {
        this.loanApprovalRepository = loanApprovalRepository;
        this.loanApprovalMapper = loanApprovalMapper;
        this.loanApplicationServiceClient = loanApplicationServiceClient;
        this.userServiceClient = userServiceClient;
        this.notificationServiceClient = notificationServiceClient;
    }

    @Override
    public LoanApprovalResponse approveLoan(Long loanId, ApproveLoanRequest request, Long approverId) {
        logger.info("Approving loan ID: {} by approver ID: {}", loanId, approverId);

        // 1. Verify approver has required role
        if (!userServiceClient.userHasAnyRole(approverId, List.of("LOAN_OFFICER", "ADMIN"))) {
            throw new BusinessException("User does not have permission to approve loans");
        }

        // 2. Get loan details from loan-application-service
        LoanDTO loan = loanApplicationServiceClient.getLoanById(loanId);

        // 3. Verify loan status is PENDING or UNDER_REVIEW
        if (!loan.getStatus().equals("PENDING") && !loan.getStatus().equals("UNDER_REVIEW")) {
            throw new BusinessException("Only PENDING or UNDER_REVIEW loans can be approved. Current status: " + loan.getStatus());
        }

        // 4. Check if loan already has an approval decision
        if (loanApprovalRepository.existsByLoanId(loanId)) {
            throw new BusinessException("Loan has already been approved or rejected");
        }

        // 5. Validate approved amount
        if (request.getApprovedAmount().compareTo(loan.getRequestedAmount()) > 0) {
            throw new BusinessException("Approved amount cannot exceed requested amount");
        }

        // 6. Create approval record
        LoanApproval approval = LoanApproval.builder()
                .loanId(loanId)
                .approverId(approverId)
                .status(LoanApproval.ApprovalStatus.APPROVED)
                .approvedAmount(request.getApprovedAmount())
                .interestRate(request.getInterestRate())
                .decisionDate(LocalDateTime.now())
                .notes(request.getNotes())
                .build();

        LoanApproval savedApproval = loanApprovalRepository.save(approval);

        // 7. Update loan status to APPROVED in loan-application-service
        loanApplicationServiceClient.updateLoanStatusToApproved(
                loanId,
                request.getApprovedAmount(),
                request.getInterestRate()
        );

        logger.info("Loan {} approved successfully by approver {}", loanId, approverId);

        // 8. Send loan approved notification
        try {
            UserDetailsDTO applicant = userServiceClient.getUserById(loan.getCustomerId());
            String applicantName = applicant.getFirstName() + " " + applicant.getLastName();
            notificationServiceClient.sendLoanApprovedNotification(
                    applicant.getEmail(),
                    applicantName,
                    loanId.toString(),
                    loan.getRequestedAmount().toString(),
                    request.getApprovedAmount().toString()
            );
        } catch (Exception e) {
            logger.error("Failed to send loan approved notification for loan: {}", loanId, e);
        }

        // 9. Build response with approver details
        LoanApprovalResponse response = loanApprovalMapper.toResponse(savedApproval);
        enrichWithUserDetails(response, approverId);

        return response;
    }

    @Override
    public LoanApprovalResponse rejectLoan(Long loanId, RejectLoanRequest request, Long approverId) {
        logger.info("Rejecting loan ID: {} by approver ID: {}", loanId, approverId);

        // 1. Verify approver has required role
        if (!userServiceClient.userHasAnyRole(approverId, List.of("LOAN_OFFICER", "ADMIN"))) {
            throw new BusinessException("User does not have permission to reject loans");
        }

        // 2. Get loan details from loan-application-service
        LoanDTO loan = loanApplicationServiceClient.getLoanById(loanId);

        // 3. Verify loan status is PENDING or UNDER_REVIEW
        if (!loan.getStatus().equals("PENDING") && !loan.getStatus().equals("UNDER_REVIEW")) {
            throw new BusinessException("Only PENDING or UNDER_REVIEW loans can be rejected. Current status: " + loan.getStatus());
        }

        // 4. Check if loan already has an approval decision
        if (loanApprovalRepository.existsByLoanId(loanId)) {
            throw new BusinessException("Loan has already been approved or rejected");
        }

        // 5. Create rejection record
        LoanApproval rejection = LoanApproval.builder()
                .loanId(loanId)
                .approverId(approverId)
                .status(LoanApproval.ApprovalStatus.REJECTED)
                .decisionDate(LocalDateTime.now())
                .rejectionReason(request.getRejectionReason())
                .notes(request.getNotes())
                .build();

        LoanApproval savedRejection = loanApprovalRepository.save(rejection);

        // 6. Update loan status to REJECTED in loan-application-service
        loanApplicationServiceClient.updateLoanStatusToRejected(loanId, request.getRejectionReason());

        logger.info("Loan {} rejected by approver {}: {}", loanId, approverId, request.getRejectionReason());

        // 7. Send loan rejected notification
        try {
            UserDetailsDTO applicant = userServiceClient.getUserById(loan.getCustomerId());
            String applicantName = applicant.getFirstName() + " " + applicant.getLastName();
            notificationServiceClient.sendLoanRejectedNotification(
                    applicant.getEmail(),
                    applicantName,
                    loanId.toString(),
                    request.getRejectionReason()
            );
        } catch (Exception e) {
            logger.error("Failed to send loan rejected notification for loan: {}", loanId, e);
        }

        // 8. Build response with approver details
        LoanApprovalResponse response = loanApprovalMapper.toResponse(savedRejection);
        enrichWithUserDetails(response, approverId);

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public LoanDTO getLoanForReview(Long loanId) {
        logger.info("Fetching loan details for review: loan ID {}", loanId);

        // Get loan details from loan-application-service
        LoanDTO loan = loanApplicationServiceClient.getLoanById(loanId);

        logger.info("Loan {} retrieved for review. Status: {}, Amount: {}",
                loanId, loan.getStatus(), loan.getRequestedAmount());

        return loan;
    }

    @Override
    @Transactional(readOnly = true)
    public LoanApprovalResponse getApprovalByLoanId(Long loanId) {
        logger.info("Fetching approval details for loan ID: {}", loanId);

        LoanApproval approval = loanApprovalRepository.findByLoanId(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Approval not found for loan ID: " + loanId));

        LoanApprovalResponse response = loanApprovalMapper.toResponse(approval);
        enrichWithUserDetails(response, approval.getApproverId());

        return response;
    }

    @Override
    public Long getPendingApprovalsCount() {
        logger.info("Fetching pending approvals count");
        // Since approvals are only created when approved/rejected,
        // we need to count loans that don't have an approval record yet
        // This would require calling the loan application service
        // For now, return count of loans with PENDING/UNDER_REVIEW status from loan service
        try {
            List<LoanDTO> allLoans = loanApplicationServiceClient.getAllLoans();
            long pendingCount = allLoans.stream()
                    .filter(loan -> "PENDING".equals(loan.getStatus()) || "UNDER_REVIEW".equals(loan.getStatus()) || "APPLIED".equals(loan.getStatus()))
                    .count();
            return pendingCount;
        } catch (Exception e) {
            logger.error("Error fetching pending approvals count: {}", e.getMessage());
            return 0L;
        }
    }

    @Override
    public Long getApprovedLoansCount() {
        logger.info("Fetching approved loans count");
        return loanApprovalRepository.countByStatus(LoanApproval.ApprovalStatus.APPROVED);
    }

    @Override
    public Long getRejectedLoansCount() {
        logger.info("Fetching rejected loans count");
        return loanApprovalRepository.countByStatus(LoanApproval.ApprovalStatus.REJECTED);
    }

    @Override
    public Long getPendingApprovalsByOfficerId(Long officerId) {
        logger.info("Fetching pending approvals count for officer ID: {}", officerId);
        try {
            // Get loans assigned to this officer
            java.util.List<com.loanmanagement.common.dto.LoanDTO> officerLoans =
                loanApplicationServiceClient.getLoansByOfficerId(officerId);

            // Count loans that are in pending/under review status
            long pendingCount = officerLoans.stream()
                .filter(loan -> {
                    String status = loan.getStatus();
                    return "PENDING".equalsIgnoreCase(status) ||
                           "APPLIED".equalsIgnoreCase(status) ||
                           "UNDER_REVIEW".equalsIgnoreCase(status);
                })
                .count();

            logger.info("Found {} pending loans for officer ID: {}", pendingCount, officerId);
            return pendingCount;
        } catch (Exception e) {
            logger.error("Error fetching pending approvals for officer ID: {}", officerId, e);
            return 0L;
        }
    }

    /**
     * Enrich response with user details
     */
    private void enrichWithUserDetails(LoanApprovalResponse response, Long approverId) {
        try {
            UserDetailsDTO approver = userServiceClient.getUserById(approverId);
            response.setApproverName(approver.getFirstName() + " " + approver.getLastName());
        } catch (Exception e) {
            logger.warn("Could not fetch approver details for user ID: {}", approverId, e);
            response.setApproverName("Unknown");
        }
    }
}
