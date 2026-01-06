package com.loanmanagement.loanapproval.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for rejecting a loan
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RejectLoanRequest {

    @NotBlank(message = "Rejection reason is required")
    @Size(min = 10, max = 1000, message = "Rejection reason must be between 10 and 1000 characters")
    private String rejectionReason;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;
}
