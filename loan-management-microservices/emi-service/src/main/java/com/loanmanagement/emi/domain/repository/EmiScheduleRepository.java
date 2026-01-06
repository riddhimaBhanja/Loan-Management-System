package com.loanmanagement.emi.domain.repository;

import com.loanmanagement.emi.domain.model.EmiSchedule;
import com.loanmanagement.emi.domain.model.EmiStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for EmiSchedule entity
 */
@Repository
public interface EmiScheduleRepository extends JpaRepository<EmiSchedule, Long> {

    /**
     * Find all EMI schedules for a loan
     */
    List<EmiSchedule> findByLoanIdOrderByEmiNumberAsc(Long loanId);

    /**
     * Find all EMI schedules for a customer
     */
    List<EmiSchedule> findByCustomerIdOrderByDueDateAsc(Long customerId);

    /**
     * Find EMI by loan ID and EMI number
     */
    Optional<EmiSchedule> findByLoanIdAndEmiNumber(Long loanId, Integer emiNumber);

    /**
     * Find EMIs by status
     */
    List<EmiSchedule> findByStatus(EmiStatus status);

    /**
     * Find EMIs by loan ID and status
     */
    List<EmiSchedule> findByLoanIdAndStatus(Long loanId, EmiStatus status);

    /**
     * Count EMIs by loan ID and status
     */
    Long countByLoanIdAndStatus(Long loanId, EmiStatus status);

    /**
     * Find overdue EMIs
     */
    @Query("SELECT e FROM EmiSchedule e WHERE e.status IN ('PENDING', 'PARTIAL_PAID') AND e.dueDate < :currentDate")
    List<EmiSchedule> findOverdueEmis(@Param("currentDate") LocalDate currentDate);

    /**
     * Find upcoming EMIs (due within next N days)
     */
    @Query("SELECT e FROM EmiSchedule e WHERE e.status = 'PENDING' " +
           "AND e.dueDate BETWEEN :startDate AND :endDate " +
           "ORDER BY e.dueDate ASC")
    List<EmiSchedule> findUpcomingEmis(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    /**
     * Find next pending EMI for a loan
     */
    @Query("SELECT e FROM EmiSchedule e WHERE e.loanId = :loanId AND e.status IN ('PENDING', 'OVERDUE', 'PARTIAL_PAID') " +
           "ORDER BY e.emiNumber ASC LIMIT 1")
    Optional<EmiSchedule> findNextPendingEmi(@Param("loanId") Long loanId);

    /**
     * Check if all EMIs are paid for a loan
     */
    @Query("SELECT CASE WHEN COUNT(e) = 0 THEN true ELSE false END FROM EmiSchedule e " +
           "WHERE e.loanId = :loanId AND e.status != 'PAID'")
    boolean areAllEmisPaid(@Param("loanId") Long loanId);

    /**
     * Get total outstanding amount for a loan
     */
    @Query("SELECT SUM(e.emiAmount) FROM EmiSchedule e " +
           "WHERE e.loanId = :loanId AND e.status != 'PAID'")
    Optional<java.math.BigDecimal> getTotalOutstandingAmount(@Param("loanId") Long loanId);

    /**
     * Count total EMIs for a loan
     */
    Long countByLoanId(Long loanId);

    /**
     * Delete all EMIs for a loan (used when recalculating)
     */
    void deleteByLoanId(Long loanId);

    /**
     * Find EMIs by customer and status
     */
    List<EmiSchedule> findByCustomerIdAndStatus(Long customerId, EmiStatus status);

    /**
     * Find overdue EMIs for a customer
     */
    @Query("SELECT e FROM EmiSchedule e WHERE e.customerId = :customerId " +
           "AND e.status IN ('PENDING', 'PARTIAL_PAID') AND e.dueDate < :currentDate " +
           "ORDER BY e.dueDate ASC")
    List<EmiSchedule> findOverdueEmisByCustomer(
            @Param("customerId") Long customerId,
            @Param("currentDate") LocalDate currentDate
    );
}
