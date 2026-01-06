package com.loanmanagement.emi.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * EmiSchedule Entity representing individual EMI installments
 *
 * NO JPA RELATIONSHIPS - Uses IDs only for microservice independence
 */
@Entity
@Table(name = "emi_schedules", indexes = {
        @Index(name = "idx_loan_id", columnList = "loan_id"),
        @Index(name = "idx_customer_id", columnList = "customer_id"),
        @Index(name = "idx_loan_emi_number", columnList = "loan_id, emi_number", unique = true),
        @Index(name = "idx_due_date", columnList = "due_date"),
        @Index(name = "idx_status", columnList = "status")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmiSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "loan_id", nullable = false)
    private Long loanId;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "emi_number", nullable = false)
    private Integer emiNumber;

    @Column(name = "emi_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal emiAmount;

    @Column(name = "principal_component", nullable = false, precision = 15, scale = 2)
    private BigDecimal principalComponent;

    @Column(name = "interest_component", nullable = false, precision = 15, scale = 2)
    private BigDecimal interestComponent;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "outstanding_balance", nullable = false, precision = 15, scale = 2)
    private BigDecimal outstandingBalance;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private EmiStatus status = EmiStatus.PENDING;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Business method to mark EMI as paid
     */
    public void markAsPaid() {
        if (!status.canMarkAsPaid()) {
            throw new IllegalStateException("Cannot mark EMI as paid from current status: " + status);
        }
        this.status = EmiStatus.PAID;
    }

    /**
     * Business method to mark EMI as overdue
     */
    public void markAsOverdue() {
        if (this.status == EmiStatus.PENDING && LocalDate.now().isAfter(this.dueDate)) {
            this.status = EmiStatus.OVERDUE;
        }
    }

    /**
     * Business method to mark EMI as partial paid
     */
    public void markAsPartialPaid() {
        if (this.status == EmiStatus.PENDING || this.status == EmiStatus.OVERDUE) {
            this.status = EmiStatus.PARTIAL_PAID;
        }
    }

    /**
     * Check if EMI is overdue
     */
    public boolean isOverdue() {
        return this.status == EmiStatus.OVERDUE ||
               (this.status == EmiStatus.PENDING && LocalDate.now().isAfter(this.dueDate));
    }
}
