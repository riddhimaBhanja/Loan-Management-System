package com.loanmanagement.loanapp.domain.model;

import com.loanmanagement.loanapp.domain.enums.EmploymentStatus;
import com.loanmanagement.loanapp.domain.enums.LoanStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Loan Entity representing loan applications (simplified for microservice)
 * NO JPA relationships - uses IDs only for loose coupling
 */
@Entity
@Table(name = "loans", indexes = {
        @Index(name = "idx_customer_id", columnList = "customer_id"),
        @Index(name = "idx_loan_type_id", columnList = "loan_type_id"),
        @Index(name = "idx_loan_officer_id", columnList = "loan_officer_id"),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_applied_date", columnList = "applied_date")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Foreign key as ID only - NO @ManyToOne relationship
    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    // Foreign key as ID only - NO @ManyToOne relationship
    @Column(name = "loan_type_id", nullable = false)
    private Long loanTypeId;

    // Foreign key as ID only - NO @ManyToOne relationship
    @Column(name = "loan_officer_id")
    private Long loanOfficerId;

    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "tenure_months", nullable = false)
    private Integer tenureMonths;

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_status", nullable = false, length = 50)
    private EmploymentStatus employmentStatus;

    @Column(name = "monthly_income", nullable = false, precision = 15, scale = 2)
    private BigDecimal monthlyIncome;

    @Column(name = "purpose", columnDefinition = "TEXT")
    private String purpose;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private LoanStatus status = LoanStatus.PENDING;

    @Column(name = "applied_date", nullable = false)
    @Builder.Default
    private LocalDateTime appliedDate = LocalDateTime.now();

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
	public void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
        if (appliedDate == null) {
            appliedDate = LocalDateTime.now();
        }
        if (status == null) {
            status = LoanStatus.PENDING;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
