package com.loanmanagement.emi.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * EmiPayment Entity representing actual payments made against EMI schedules
 *
 * NO JPA RELATIONSHIPS - Uses IDs only for microservice independence
 */
@Entity
@Table(name = "emi_payments", indexes = {
        @Index(name = "idx_emi_schedule_id", columnList = "emi_schedule_id"),
        @Index(name = "idx_loan_id", columnList = "loan_id"),
        @Index(name = "idx_payment_date", columnList = "payment_date"),
        @Index(name = "idx_transaction_ref", columnList = "transaction_reference")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmiPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "emi_schedule_id", nullable = false)
    private Long emiScheduleId;

    @Column(name = "loan_id", nullable = false)
    private Long loanId;

    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "late_fee", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal lateFee = BigDecimal.ZERO;

    @Column(name = "total_paid", precision = 15, scale = 2)
    private BigDecimal totalPaid;  // amount + lateFee

    @Column(name = "payment_date", nullable = false)
    private LocalDate paymentDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, length = 50)
    private PaymentMethod paymentMethod;

    @Column(name = "transaction_reference", length = 100)
    private String transactionReference;

    @Column(name = "paid_by", nullable = false)
    private Long paidBy;  // User ID who recorded the payment

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (paymentDate == null) {
            paymentDate = LocalDate.now();
        }
    }

    /**
     * Validate payment amount
     */
    public boolean isPaymentValid() {
        return amount != null && amount.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Check if transaction reference is required
     */
    public boolean requiresTransactionReference() {
        return paymentMethod != null && paymentMethod.requiresTransactionReference();
    }
}
