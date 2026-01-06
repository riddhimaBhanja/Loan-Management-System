package com.loanmanagement.auth.application.dto.request;

import com.loanmanagement.auth.domain.model.RoleType;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * Request DTO for updating user roles
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserRolesRequest {

    @NotEmpty(message = "At least one role is required")
    private Set<RoleType> roles;
}
