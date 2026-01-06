package com.loanmanagement.loanapp.domain.service;

import com.loanmanagement.common.exception.BusinessException;
import com.loanmanagement.common.exception.ResourceNotFoundException;
import com.loanmanagement.loanapp.application.dto.request.CreateLoanTypeRequest;
import com.loanmanagement.loanapp.application.dto.request.UpdateLoanTypeRequest;
import com.loanmanagement.loanapp.application.dto.response.LoanTypeResponse;
import com.loanmanagement.loanapp.application.mapper.LoanTypeMapper;
import com.loanmanagement.loanapp.domain.model.LoanType;
import com.loanmanagement.loanapp.domain.repository.LoanTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for LoanType operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LoanTypeServiceImpl implements LoanTypeService {

    private final LoanTypeRepository loanTypeRepository;
    private final LoanTypeMapper loanTypeMapper;

    @Override
    public LoanTypeResponse createLoanType(CreateLoanTypeRequest request) {
        log.info("Creating new loan type: {}", request.getName());

        // Validate loan type name doesn't already exist
        if (loanTypeRepository.existsByName(request.getName())) {
            throw new BusinessException("Loan type with name '" + request.getName() + "' already exists");
        }

        // Validate amount range
        if (request.getMinAmount().compareTo(request.getMaxAmount()) > 0) {
            throw new BusinessException("Minimum amount cannot be greater than maximum amount");
        }

        // Validate tenure range
        if (request.getMinTenureMonths() > request.getMaxTenureMonths()) {
            throw new BusinessException("Minimum tenure cannot be greater than maximum tenure");
        }

        LoanType loanType = loanTypeMapper.toEntity(request);
        LoanType savedLoanType = loanTypeRepository.save(loanType);

        log.info("Loan type created successfully with ID: {}", savedLoanType.getId());
        return loanTypeMapper.toResponse(savedLoanType);
    }

    @Override
    @Transactional(readOnly = true)
    public LoanTypeResponse getLoanTypeById(Long id) {
        log.debug("Fetching loan type with ID: {}", id);
        LoanType loanType = loanTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loan type not found with ID: " + id));
        return loanTypeMapper.toResponse(loanType);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoanTypeResponse> getAllLoanTypes() {
        log.debug("Fetching all loan types");
        return loanTypeRepository.findAll().stream()
                .map(loanTypeMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoanTypeResponse> getActiveLoanTypes() {
        log.debug("Fetching active loan types");
        return loanTypeRepository.findByIsActiveTrue().stream()
                .map(loanTypeMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public LoanTypeResponse updateLoanType(Long id, UpdateLoanTypeRequest request) {
        log.info("Updating loan type with ID: {}", id);

        LoanType loanType = loanTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loan type not found with ID: " + id));

        // Validate amount range if provided
        if (request.getMinAmount() != null && request.getMaxAmount() != null) {
            if (request.getMinAmount().compareTo(request.getMaxAmount()) > 0) {
                throw new BusinessException("Minimum amount cannot be greater than maximum amount");
            }
        }

        // Validate tenure range if provided
        if (request.getMinTenureMonths() != null && request.getMaxTenureMonths() != null) {
            if (request.getMinTenureMonths() > request.getMaxTenureMonths()) {
                throw new BusinessException("Minimum tenure cannot be greater than maximum tenure");
            }
        }

        loanTypeMapper.updateEntityFromRequest(request, loanType);
        LoanType updatedLoanType = loanTypeRepository.save(loanType);

        log.info("Loan type updated successfully with ID: {}", updatedLoanType.getId());
        return loanTypeMapper.toResponse(updatedLoanType);
    }

    @Override
    public void deleteLoanType(Long id) {
        log.info("Deleting loan type with ID: {}", id);

        LoanType loanType = loanTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loan type not found with ID: " + id));

        // Soft delete by marking as inactive
        loanType.setIsActive(false);
        loanTypeRepository.save(loanType);

        log.info("Loan type marked as inactive with ID: {}", id);
    }
}
