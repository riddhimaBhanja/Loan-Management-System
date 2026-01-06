package com.loanmanagement.loanapp.domain.service;

import com.loanmanagement.common.exception.BusinessException;
import com.loanmanagement.common.exception.ResourceNotFoundException;
import com.loanmanagement.loanapp.application.dto.request.AssignOfficerRequest;
import com.loanmanagement.loanapp.application.dto.request.LoanApplicationRequest;
import com.loanmanagement.loanapp.application.dto.response.LoanResponse;
import com.loanmanagement.loanapp.application.mapper.LoanMapper;
import com.loanmanagement.loanapp.domain.enums.LoanStatus;
import com.loanmanagement.loanapp.domain.model.Loan;
import com.loanmanagement.loanapp.domain.model.LoanType;
import com.loanmanagement.loanapp.domain.repository.LoanRepository;
import com.loanmanagement.loanapp.domain.repository.LoanTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for Loan Application operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LoanApplicationServiceImpl implements LoanApplicationService {

    private final LoanRepository loanRepository;
    private final LoanTypeRepository loanTypeRepository;
    private final LoanMapper loanMapper;

    @Override
    public LoanResponse createLoanApplication(LoanApplicationRequest request, Long customerId) {
        log.info("Creating loan application for customer ID: {}", customerId);

        // Validate loan type exists and is active
        LoanType loanType = loanTypeRepository.findById(request.getLoanTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Loan type not found with ID: " + request.getLoanTypeId()));

        if (!loanType.getIsActive()) {
            throw new BusinessException("Loan type is not active");
        }

        // Validate amount is within loan type limits
        if (!loanType.isAmountValid(request.getAmount())) {
            throw new BusinessException(
                    String.format("Loan amount must be between %s and %s",
                            loanType.getMinAmount(), loanType.getMaxAmount())
            );
        }

        // Validate tenure is within loan type limits
        if (!loanType.isTenureValid(request.getTenureMonths())) {
            throw new BusinessException(
                    String.format("Loan tenure must be between %d and %d months",
                            loanType.getMinTenureMonths(), loanType.getMaxTenureMonths())
            );
        }

        // Create loan entity
        Loan loan = loanMapper.toEntity(request, customerId);
        Loan savedLoan = loanRepository.save(loan);

        log.info("Loan application created successfully with ID: {}", savedLoan.getId());
        return enrichLoanResponse(savedLoan);
    }

    @Override
    @Transactional(readOnly = true)
    public LoanResponse getLoanById(Long id) {
        log.debug("Fetching loan with ID: {}", id);
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found with ID: " + id));
        return enrichLoanResponse(loan);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoanResponse> getCustomerLoans(Long customerId) {
        log.debug("Fetching loans for customer ID: {}", customerId);
        return loanRepository.findByCustomerId(customerId).stream()
                .map(this::enrichLoanResponse)
                .collect(Collectors.toList());
    }

    /**
     * Enrich loan response with additional display fields
     */
    private LoanResponse enrichLoanResponse(Loan loan) {
        LoanResponse response = loanMapper.toResponse(loan);

        // Set aliases for frontend compatibility
        response.setRequestedAmount(loan.getAmount());
        response.setAppliedAt(loan.getAppliedDate());
        response.setApplicationNumber("LN" + String.format("%06d", loan.getId()));

        // Fetch and set loan type name
        loanTypeRepository.findById(loan.getLoanTypeId())
                .ifPresent(loanType -> response.setLoanTypeName(loanType.getName()));

        // Fetch customer name from auth-service
        try {
            // Use UserServiceClient to fetch customer details
            // For now, set a placeholder - you can uncomment and inject UserServiceClient later
            response.setCustomerName("Customer #" + loan.getCustomerId());
        } catch (Exception e) {
            log.warn("Could not fetch customer name for ID {}: {}", loan.getCustomerId(), e.getMessage());
            response.setCustomerName("Customer #" + loan.getCustomerId());
        }

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoanResponse> getAllLoans() {
        log.debug("Fetching all loans");
        return loanRepository.findAll().stream()
                .map(this::enrichLoanResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoanResponse> getLoansByStatus(String status) {
        log.debug("Fetching loans with status: {}", status);
        try {
            LoanStatus loanStatus = LoanStatus.valueOf(status.toUpperCase());
            return loanRepository.findByStatus(loanStatus).stream()
                    .map(this::enrichLoanResponse)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Invalid loan status: " + status);
        }
    }

    @Override
    public LoanResponse assignOfficer(Long loanId, AssignOfficerRequest request) {
        log.info("Assigning officer {} to loan {}", request.getLoanOfficerId(), loanId);

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found with ID: " + loanId));

        loan.setLoanOfficerId(request.getLoanOfficerId());
        Loan updatedLoan = loanRepository.save(loan);

        log.info("Loan officer assigned successfully to loan {}", loanId);
        return enrichLoanResponse(updatedLoan);
    }

    @Override
    public LoanResponse unassignOfficer(Long loanId) {
        log.info("Unassigning officer from loan {}", loanId);

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found with ID: " + loanId));

        loan.setLoanOfficerId(null);
        Loan updatedLoan = loanRepository.save(loan);

        log.info("Loan officer unassigned successfully from loan {}", loanId);
        return enrichLoanResponse(updatedLoan);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoanResponse> getLoansByOfficerId(Long officerId) {
        log.debug("Fetching loans for officer ID: {}", officerId);
        return loanRepository.findByLoanOfficerId(officerId).stream()
                .map(this::enrichLoanResponse)
                .collect(Collectors.toList());
    }

    // Internal service-to-service methods
    @Override
    public void approveLoan(Long loanId, java.math.BigDecimal approvedAmount, java.math.BigDecimal interestRate) {
        log.info("Approving loan {} via internal API", loanId);

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found with ID: " + loanId));

        loan.setStatus(LoanStatus.APPROVED);
        loanRepository.save(loan);

        log.info("Loan {} status updated to APPROVED", loanId);
    }

    @Override
    public void rejectLoan(Long loanId, String reason) {
        log.info("Rejecting loan {} via internal API: {}", loanId, reason);

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found with ID: " + loanId));

        loan.setStatus(LoanStatus.REJECTED);
        loanRepository.save(loan);

        log.info("Loan {} status updated to REJECTED", loanId);
    }

    @Override
    public void disburseLoan(Long loanId, java.time.LocalDate disbursementDate, String disbursementMethod, String referenceNumber) {
        log.info("Disbursing loan {} via internal API", loanId);

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found with ID: " + loanId));

        loan.setStatus(LoanStatus.DISBURSED);
        loanRepository.save(loan);

        log.info("Loan {} status updated to DISBURSED", loanId);
    }

    @Override
    public void closeLoan(Long loanId) {
        log.info("Closing loan {} via internal API", loanId);

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found with ID: " + loanId));

        loan.setStatus(LoanStatus.CLOSED);
        loanRepository.save(loan);

        log.info("Loan {} status updated to CLOSED", loanId);
    }
}
