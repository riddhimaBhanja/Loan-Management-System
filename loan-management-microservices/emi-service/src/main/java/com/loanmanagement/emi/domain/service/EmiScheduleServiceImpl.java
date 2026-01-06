package com.loanmanagement.emi.domain.service;

import com.loanmanagement.common.dto.GenerateEmiRequest;
import com.loanmanagement.common.exception.BusinessException;
import com.loanmanagement.common.exception.ResourceNotFoundException;
import com.loanmanagement.emi.application.dto.response.EmiScheduleResponse;
import com.loanmanagement.emi.application.dto.response.EmiSummaryResponse;
import com.loanmanagement.emi.application.mapper.EmiScheduleMapper;
import com.loanmanagement.emi.domain.model.EmiSchedule;
import com.loanmanagement.emi.domain.model.EmiStatus;
import com.loanmanagement.emi.domain.repository.EmiScheduleRepository;
import com.loanmanagement.emi.infrastructure.client.UserServiceClient;
import com.loanmanagement.emi.shared.constants.MessageConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of EmiScheduleService
 */
@Service
@Transactional
public class EmiScheduleServiceImpl implements EmiScheduleService {

    private static final Logger logger = LoggerFactory.getLogger(EmiScheduleServiceImpl.class);

    @Autowired
    private EmiScheduleRepository emiScheduleRepository;

    @Autowired
    private EmiCalculationService emiCalculationService;

    @Autowired
    private EmiScheduleMapper emiScheduleMapper;

    @Autowired
    private UserServiceClient userServiceClient;

    @Autowired
    private com.loanmanagement.emi.domain.repository.EmiPaymentRepository emiPaymentRepository;

    @Override
    public List<EmiScheduleResponse> generateEmiSchedule(GenerateEmiRequest request) {
        logger.info("Generating EMI schedule for loan ID: {}", request.getLoanId());

        // Check if EMI schedule already exists
        List<EmiSchedule> existingSchedules = emiScheduleRepository.findByLoanIdOrderByEmiNumberAsc(request.getLoanId());
        if (!existingSchedules.isEmpty()) {
            logger.warn("EMI schedule already exists for loan ID: {}", request.getLoanId());
            throw new BusinessException(MessageConstants.EMI_ALREADY_EXISTS);
        }

        // Generate EMI schedule
        List<EmiSchedule> schedules = emiCalculationService.generateEmiSchedule(
                request.getLoanId(),
                request.getCustomerId(),
                request.getPrincipal(),
                request.getInterestRate(),
                request.getTenureMonths(),
                request.getStartDate()
        );

        // Save all schedules
        List<EmiSchedule> savedSchedules = emiScheduleRepository.saveAll(schedules);

        logger.info("EMI schedule generated and saved for loan ID: {} with {} installments",
                request.getLoanId(), savedSchedules.size());

        return emiScheduleMapper.toResponseList(savedSchedules);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmiScheduleResponse> getEmiSchedule(Long loanId) {
        logger.info("Fetching EMI schedule for loan ID: {}", loanId);

        List<EmiSchedule> schedules = emiScheduleRepository.findByLoanIdOrderByEmiNumberAsc(loanId);

        if (schedules.isEmpty()) {
            logger.warn("No EMI schedule found for loan ID: {}", loanId);
            throw new ResourceNotFoundException(MessageConstants.EMI_SCHEDULE_NOT_FOUND);
        }

        return emiScheduleMapper.toResponseList(schedules);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmiScheduleResponse> getEmiScheduleByCustomer(Long customerId) {
        logger.info("Fetching EMI schedules for customer ID: {}", customerId);

        List<EmiSchedule> schedules = emiScheduleRepository.findByCustomerIdOrderByDueDateAsc(customerId);

        return emiScheduleMapper.toResponseList(schedules);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmiScheduleResponse> getMyEmiSchedule() {
        logger.info("Fetching EMI schedules for currently logged-in customer");

        Long currentUserId = getCurrentUserId();
        logger.debug("Current user ID: {}", currentUserId);

        List<EmiSchedule> schedules = emiScheduleRepository.findByCustomerIdOrderByDueDateAsc(currentUserId);

        logger.info("Found {} EMI schedules for current customer", schedules.size());

        return emiScheduleMapper.toResponseList(schedules);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmiScheduleResponse> getPendingEmisForCurrentUser() {
        logger.info("Fetching pending EMIs for currently logged-in customer");

        Long currentUserId = getCurrentUserId();
        logger.debug("Current user ID: {}", currentUserId);

        List<EmiSchedule> allSchedules = emiScheduleRepository.findByCustomerIdOrderByDueDateAsc(currentUserId);

        // Filter for PENDING status only
        List<EmiSchedule> pendingSchedules = allSchedules.stream()
                .filter(schedule -> schedule.getStatus() == EmiStatus.PENDING)
                .collect(Collectors.toList());

        logger.info("Found {} pending EMIs for current customer", pendingSchedules.size());

        return emiScheduleMapper.toResponseList(pendingSchedules);
    }

    @Override
    @Transactional(readOnly = true)
    public EmiSummaryResponse getEmiSummary(Long loanId) {
        logger.info("Fetching EMI summary for loan ID: {}", loanId);

        List<EmiSchedule> schedules = emiScheduleRepository.findByLoanIdOrderByEmiNumberAsc(loanId);

        if (schedules.isEmpty()) {
            logger.warn("No EMI schedule found for loan ID: {}", loanId);
            throw new ResourceNotFoundException(MessageConstants.EMI_SCHEDULE_NOT_FOUND);
        }

        // Calculate summary
        int totalEmis = schedules.size();
        int paidEmis = 0;
        int pendingEmis = 0;
        int overdueEmis = 0;
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal paidAmount = BigDecimal.ZERO;

        for (EmiSchedule schedule : schedules) {
            totalAmount = totalAmount.add(schedule.getEmiAmount());

            switch (schedule.getStatus()) {
                case PAID:
                    paidEmis++;
                    paidAmount = paidAmount.add(schedule.getEmiAmount());
                    break;
                case PENDING:
                case PARTIAL_PAID:
                    if (schedule.isOverdue()) {
                        overdueEmis++;
                    } else {
                        pendingEmis++;
                    }
                    break;
                case OVERDUE:
                    overdueEmis++;
                    break;
            }
        }

        BigDecimal pendingAmount = totalAmount.subtract(paidAmount);
        BigDecimal outstandingAmount = emiScheduleRepository.getTotalOutstandingAmount(loanId)
                .orElse(BigDecimal.ZERO);

        return EmiSummaryResponse.builder()
                .loanId(loanId)
                .customerId(schedules.get(0).getCustomerId())
                .totalEmis(totalEmis)
                .paidEmis(paidEmis)
                .pendingEmis(pendingEmis)
                .overdueEmis(overdueEmis)
                .totalAmount(totalAmount)
                .paidAmount(paidAmount)
                .pendingAmount(pendingAmount)
                .outstandingAmount(outstandingAmount)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmiScheduleResponse> getOverdueEmis() {
        logger.info("Fetching overdue EMIs");

        LocalDate today = LocalDate.now();
        List<EmiSchedule> overdueEmis = emiScheduleRepository.findOverdueEmis(today);

        return emiScheduleMapper.toResponseList(overdueEmis);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmiScheduleResponse> getOverdueEmisByCustomer(Long customerId) {
        logger.info("Fetching overdue EMIs for customer ID: {}", customerId);

        LocalDate today = LocalDate.now();
        List<EmiSchedule> overdueEmis = emiScheduleRepository.findOverdueEmisByCustomer(customerId, today);

        return emiScheduleMapper.toResponseList(overdueEmis);
    }

    @Override
    @Scheduled(cron = "${emi.schedule.overdue-check-cron}")
    public int markOverdueEmis() {
        logger.info("Running scheduled job to mark overdue EMIs");

        LocalDate today = LocalDate.now();
        List<EmiSchedule> overdueEmis = emiScheduleRepository.findOverdueEmis(today);

        int count = 0;
        for (EmiSchedule emi : overdueEmis) {
            if (emi.getStatus() == EmiStatus.PENDING || emi.getStatus() == EmiStatus.PARTIAL_PAID) {
                emi.markAsOverdue();
                count++;
                logger.debug("Marked EMI #{} for loan {} as overdue", emi.getEmiNumber(), emi.getLoanId());
            }
        }

        if (count > 0) {
            emiScheduleRepository.saveAll(overdueEmis);
            logger.info("Marked {} EMIs as overdue", count);
        } else {
            logger.info("No EMIs to mark as overdue");
        }

        return count;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean verifyAllEmisPaid(Long loanId) {
        logger.info("Verifying if all EMIs are paid for loan ID: {}", loanId);

        boolean allPaid = emiScheduleRepository.areAllEmisPaid(loanId);

        logger.info("Loan ID: {} - All EMIs paid: {}", loanId, allPaid);

        return allPaid;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getOutstandingAmount(Long loanId) {
        logger.info("Fetching outstanding amount for loan ID: {}", loanId);

        BigDecimal outstanding = emiScheduleRepository.getTotalOutstandingAmount(loanId)
                .orElse(BigDecimal.ZERO);

        logger.info("Outstanding amount for loan ID {}: {}", loanId, outstanding);

        return outstanding;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalCollected() {
        logger.info("Fetching total collected EMI amount");

        List<EmiSchedule> paidEmis = emiScheduleRepository.findByStatus(EmiStatus.PAID);
        BigDecimal totalCollected = paidEmis.stream()
                .map(EmiSchedule::getEmiAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        logger.info("Total EMI collected: {}", totalCollected);
        return totalCollected;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalPending() {
        logger.info("Fetching total pending EMI amount");

        List<EmiSchedule> pendingEmis = emiScheduleRepository.findByStatus(EmiStatus.PENDING);
        List<EmiSchedule> partialPaidEmis = emiScheduleRepository.findByStatus(EmiStatus.PARTIAL_PAID);
        List<EmiSchedule> overdueEmis = emiScheduleRepository.findByStatus(EmiStatus.OVERDUE);

        BigDecimal totalPending = pendingEmis.stream()
                .map(EmiSchedule::getEmiAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        totalPending = totalPending.add(partialPaidEmis.stream()
                .map(EmiSchedule::getEmiAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        totalPending = totalPending.add(overdueEmis.stream()
                .map(EmiSchedule::getEmiAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        logger.info("Total pending EMI amount: {}", totalPending);
        return totalPending;
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.Map<String, Object> getOverdueStatistics() {
        logger.info("Fetching overdue statistics");

        LocalDate today = LocalDate.now();
        List<EmiSchedule> overdueEmis = emiScheduleRepository.findOverdueEmis(today);

        BigDecimal overdueAmount = overdueEmis.stream()
                .map(EmiSchedule::getEmiAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("overdueCount", overdueEmis.size());
        stats.put("overdueAmount", overdueAmount);

        logger.info("Overdue statistics - Count: {}, Amount: {}", overdueEmis.size(), overdueAmount);
        return stats;
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.Map<String, Object> getPaymentStatistics(String startDate, String endDate) {
        logger.info("Fetching payment statistics from {} to {}", startDate, endDate);

        LocalDate start = startDate != null ? LocalDate.parse(startDate) : LocalDate.now().minusMonths(1);
        LocalDate end = endDate != null ? LocalDate.parse(endDate) : LocalDate.now();

        List<com.loanmanagement.emi.domain.model.EmiPayment> payments =
                emiPaymentRepository.findPaymentsBetweenDates(start, end);

        BigDecimal totalAmount = payments.stream()
                .map(com.loanmanagement.emi.domain.model.EmiPayment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("paymentCount", payments.size());
        stats.put("totalAmount", totalAmount);
        stats.put("startDate", start.toString());
        stats.put("endDate", end.toString());

        logger.info("Payment statistics - Count: {}, Amount: {}", payments.size(), totalAmount);
        return stats;
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.Map<String, Object> getCustomerEmiSummary(Long customerId) {
        logger.info("Fetching EMI summary for customer ID: {}", customerId);

        // Get all EMI schedules for customer
        List<EmiSchedule> allSchedules = emiScheduleRepository.findByCustomerIdOrderByDueDateAsc(customerId);

        if (allSchedules.isEmpty()) {
            logger.warn("No EMI schedules found for customer ID: {}", customerId);
            return new java.util.HashMap<>();
        }

        // Calculate total pending amount
        BigDecimal totalPending = allSchedules.stream()
                .filter(e -> e.getStatus() != EmiStatus.PAID)
                .map(EmiSchedule::getEmiAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Get overdue count
        LocalDate today = LocalDate.now();
        long overdueCount = allSchedules.stream()
                .filter(e -> e.getStatus() == EmiStatus.OVERDUE ||
                           (e.getStatus() == EmiStatus.PENDING && e.getDueDate().isBefore(today)))
                .count();

        // Find next pending EMI
        EmiSchedule nextEmi = allSchedules.stream()
                .filter(e -> e.getStatus() == EmiStatus.PENDING || e.getStatus() == EmiStatus.OVERDUE)
                .findFirst()
                .orElse(null);

        // Build summary map
        java.util.Map<String, Object> summary = new java.util.HashMap<>();
        summary.put("totalPending", totalPending);
        summary.put("overdueCount", overdueCount);

        if (nextEmi != null) {
            summary.put("nextEmiAmount", nextEmi.getEmiAmount());
            summary.put("nextEmiDueDate", nextEmi.getDueDate().toString());
            summary.put("nextEmiLoanId", nextEmi.getLoanId());
            logger.info("Next EMI for customer {}: Amount={}, Due Date={}, Loan ID={}",
                    customerId, nextEmi.getEmiAmount(), nextEmi.getDueDate(), nextEmi.getLoanId());
        } else {
            logger.info("No pending EMIs found for customer ID: {}", customerId);
        }

        return summary;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmiScheduleResponse> getUpcomingEmis(Integer daysAhead) {
        logger.info("Fetching EMIs due within next {} days", daysAhead);

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(daysAhead);

        List<EmiSchedule> upcomingEmis = emiScheduleRepository.findUpcomingEmis(startDate, endDate);

        logger.info("Found {} upcoming EMIs due between {} and {}", upcomingEmis.size(), startDate, endDate);

        return upcomingEmis.stream()
                .map(emiScheduleMapper::toResponse)
                .toList();
    }

    /**
     * Get current user ID from security context
     */
    private Long getCurrentUserId() {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
                String username = ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
                return userServiceClient.getUserIdByUsername(username);
            } else {
                // If principal is a string (username), use it directly
                return userServiceClient.getUserIdByUsername(principal.toString());
            }
        } catch (Exception e) {
            logger.error("Failed to get current user ID from security context", e);
            throw new BusinessException("Failed to get current user information");
        }
    }
}
