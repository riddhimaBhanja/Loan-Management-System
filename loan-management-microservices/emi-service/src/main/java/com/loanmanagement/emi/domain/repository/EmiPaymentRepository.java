package com.loanmanagement.emi.domain.repository;

import com.loanmanagement.emi.domain.model.EmiPayment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for EmiPayment entity
 */
@Repository
public interface EmiPaymentRepository extends JpaRepository<EmiPayment, Long> {

    /**
     * Find all payments for a loan
     */
    List<EmiPayment> findByLoanIdOrderByPaymentDateDesc(Long loanId);

    /**
     * Find payments for a loan (paginated)
     */
    Page<EmiPayment> findByLoanId(Long loanId, Pageable pageable);

    /**
     * Find payment for specific EMI schedule
     */
    Optional<EmiPayment> findByEmiScheduleId(Long emiScheduleId);

    /**
     * Find all payments for specific EMI schedule
     */
    List<EmiPayment> findAllByEmiScheduleId(Long emiScheduleId);

    /**
     * Find payments made between dates
     */
    @Query("SELECT p FROM EmiPayment p WHERE p.paymentDate BETWEEN :startDate AND :endDate " +
           "ORDER BY p.paymentDate DESC")
    List<EmiPayment> findPaymentsBetweenDates(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    /**
     * Find payments by transaction reference
     */
    Optional<EmiPayment> findByTransactionReference(String transactionReference);

    /**
     * Get total payments for a loan
     */
    @Query("SELECT SUM(p.amount) FROM EmiPayment p WHERE p.loanId = :loanId")
    Optional<BigDecimal> getTotalPaymentsForLoan(@Param("loanId") Long loanId);

    /**
     * Count payments for a loan
     */
    Long countByLoanId(Long loanId);

    /**
     * Find recent payments (last N days)
     */
    @Query("SELECT p FROM EmiPayment p WHERE p.paymentDate >= :fromDate " +
           "ORDER BY p.paymentDate DESC")
    List<EmiPayment> findRecentPayments(@Param("fromDate") LocalDate fromDate);

    /**
     * Find payments recorded by specific user
     */
    Page<EmiPayment> findByPaidBy(Long userId, Pageable pageable);

    /**
     * Get total amount paid for specific EMI schedule
     */
    @Query("SELECT SUM(p.amount) FROM EmiPayment p WHERE p.emiScheduleId = :emiScheduleId")
    Optional<BigDecimal> getTotalPaidForEmi(@Param("emiScheduleId") Long emiScheduleId);
}
