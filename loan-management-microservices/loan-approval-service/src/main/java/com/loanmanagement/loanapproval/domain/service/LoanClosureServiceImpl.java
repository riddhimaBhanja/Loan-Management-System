package com.loanmanagement.loanapproval.domain.service;

import com.loanmanagement.common.dto.LoanDTO;
import com.loanmanagement.common.dto.UserDetailsDTO;
import com.loanmanagement.common.exception.BusinessException;
import com.loanmanagement.loanapproval.infrastructure.client.EmiServiceClient;
import com.loanmanagement.loanapproval.infrastructure.client.LoanApplicationServiceClient;
import com.loanmanagement.loanapproval.infrastructure.client.NotificationServiceClient;
import com.loanmanagement.loanapproval.infrastructure.client.UserServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of LoanClosureService
 */
@Service
@Transactional
public class LoanClosureServiceImpl implements LoanClosureService {

    private static final Logger logger = LoggerFactory.getLogger(LoanClosureServiceImpl.class);

    private final LoanApplicationServiceClient loanApplicationServiceClient;
    private final EmiServiceClient emiServiceClient;
    private final UserServiceClient userServiceClient;
    private final NotificationServiceClient notificationServiceClient;

    public LoanClosureServiceImpl(
            LoanApplicationServiceClient loanApplicationServiceClient,
            EmiServiceClient emiServiceClient,
            UserServiceClient userServiceClient,
            NotificationServiceClient notificationServiceClient) {
        this.loanApplicationServiceClient = loanApplicationServiceClient;
        this.emiServiceClient = emiServiceClient;
        this.userServiceClient = userServiceClient;
        this.notificationServiceClient = notificationServiceClient;
    }

    @Override
    public LoanDTO closeLoan(Long loanId) {
        logger.info("Closing loan ID: {}", loanId);

        // 1. Get loan details from loan-application-service
        LoanDTO loan = loanApplicationServiceClient.getLoanById(loanId);

        // 2. Verify loan status is DISBURSED
        if (!loan.getStatus().equals("DISBURSED")) {
            throw new BusinessException("Only DISBURSED loans can be closed. Current status: " + loan.getStatus());
        }

        // 3. Verify all EMIs are paid via emi-service
        boolean allEmisPaid = emiServiceClient.areAllEmisPaid(loanId);
        if (!allEmisPaid) {
            throw new BusinessException("Cannot close loan - not all EMIs are paid");
        }

        // 4. Update loan status to CLOSED in loan-application-service
        loanApplicationServiceClient.updateLoanStatusToClosed(loanId);

        logger.info("Loan {} closed successfully", loanId);

        // 5. Send loan closed notification
        try {
            UserDetailsDTO applicant = userServiceClient.getUserById(loan.getCustomerId());
            String applicantName = applicant.getFirstName() + " " + applicant.getLastName();
            notificationServiceClient.sendLoanClosedNotification(
                    applicant.getEmail(),
                    applicantName,
                    loanId.toString()
            );
        } catch (Exception e) {
            logger.error("Failed to send loan closed notification for loan: {}", loanId, e);
        }

        // 6. Return updated loan details
        return loanApplicationServiceClient.getLoanById(loanId);
    }
}
