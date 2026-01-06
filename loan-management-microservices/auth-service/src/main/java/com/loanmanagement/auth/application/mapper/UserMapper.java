package com.loanmanagement.auth.application.mapper;

import com.loanmanagement.auth.application.dto.request.CreateUserRequest;
import com.loanmanagement.auth.application.dto.request.RegisterRequest;
import com.loanmanagement.auth.application.dto.request.UpdateUserRequest;
import com.loanmanagement.auth.application.dto.response.UserResponse;
import com.loanmanagement.auth.domain.model.RoleType;
import com.loanmanagement.auth.domain.model.User;
import com.loanmanagement.auth.domain.model.UserRole;
import com.loanmanagement.common.dto.UserDetailsDTO;
import org.mapstruct.*;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(target = "roles", source = "roles", qualifiedByName = "rolesToRoleTypes")
    UserResponse toResponse(User user);

    @Mapping(target = "roles", source = "roles", qualifiedByName = "rolesToStrings")
    UserDetailsDTO toUserDetailsDTO(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toEntity(RegisterRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toEntity(CreateUserRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(UpdateUserRequest request, @MappingTarget User user);

    @Named("rolesToRoleTypes")
    default Set<RoleType> rolesToRoleTypes(Set<UserRole> roles) {
        return roles.stream()
                .map(UserRole::getRole)
                .collect(Collectors.toSet());
    }

    @Named("rolesToStrings")
    default Set<String> rolesToStrings(Set<UserRole> roles) {
        return roles.stream()
                .map(role -> role.getRole().name())
                .collect(Collectors.toSet());
    }
}
