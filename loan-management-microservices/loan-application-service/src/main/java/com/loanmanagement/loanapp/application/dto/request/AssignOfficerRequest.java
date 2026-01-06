package com.loanmanagement.loanapp.application.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for assigning/reassigning loan officer to a loan
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignOfficerRequest {

    @NotNull(message = "Loan officer ID is required")
    private Long loanOfficerId;

    private String remarks;
}
