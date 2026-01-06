package com.loanmanagement.loanapproval.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Request DTO for disbursing a loan
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DisburseLoanRequest {

    @NotNull(message = "Disbursement date is required")
    private LocalDate disbursementDate;

    @NotBlank(message = "Disbursement method is required")
    @Size(max = 50, message = "Disbursement method cannot exceed 50 characters")
    private String disbursementMethod;

    @NotBlank(message = "Reference number is required")
    @Size(max = 100, message = "Reference number cannot exceed 100 characters")
    private String referenceNumber;

    @Size(max = 500, message = "Remarks cannot exceed 500 characters")
    private String remarks;
}
