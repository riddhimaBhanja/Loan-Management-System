package com.loanmanagement.auth.domain.repository;

import com.loanmanagement.auth.domain.model.RoleType;
import com.loanmanagement.auth.domain.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for UserRole entity
 */
@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    /**
     * Find user roles by user ID
     */
    List<UserRole> findByUserId(Long userId);

    /**
     * Find specific user role
     */
    Optional<UserRole> findByUserIdAndRole(Long userId, RoleType role);

    /**
     * Delete all roles for a user
     */
    void deleteByUserId(Long userId);

    /**
     * Check if user has specific role
     */
    boolean existsByUserIdAndRole(Long userId, RoleType role);
    long countByRole(RoleType role);
}
