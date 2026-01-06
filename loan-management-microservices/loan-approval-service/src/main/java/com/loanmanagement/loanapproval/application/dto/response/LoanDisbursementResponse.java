package com.loanmanagement.loanapproval.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Response DTO for loan disbursement
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanDisbursementResponse {

    private Long id;
    private Long loanId;
    private Long disbursedBy;
    private String disbursedByName;
    private BigDecimal amount;
    private LocalDate disbursementDate;
    private String disbursementMethod;
    private String referenceNumber;
    private String remarks;
    private LocalDateTime createdAt;
}
