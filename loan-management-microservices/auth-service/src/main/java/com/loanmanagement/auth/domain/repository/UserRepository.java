package com.loanmanagement.auth.domain.repository;

import com.loanmanagement.auth.domain.model.RoleType;
import com.loanmanagement.auth.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by username
     */
    Optional<User> findByUsername(String username);

    /**
     * Find user by email
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if username exists
     */
    boolean existsByUsername(String username);

    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);

    /**
     * Find active users
     */
    Page<User> findByIsActive(Boolean isActive, Pageable pageable);

    /**
     * Find users by role
     */
    @Query("SELECT DISTINCT u FROM User u JOIN u.roles r WHERE r.role = :roleType")
    Page<User> findByRole(@Param("roleType") RoleType roleType, Pageable pageable);

    /**
     * Find user with roles by username (for authentication)
     */
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.username = :username")
    Optional<User> findByUsernameWithRoles(@Param("username") String username);

    /**
     * Find user with roles by ID
     */
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.id = :id")
    Optional<User> findByIdWithRoles(@Param("id") Long id);

    /**
     * Find users by list of IDs (for batch operations)
     */
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.id IN :ids")
    List<User> findByIdInWithRoles(@Param("ids") List<Long> ids);

    /**
     * Count users by active status
     */
    Long countByIsActive(Boolean isActive);

    /**
     * Count users by role
     */
    @Query("SELECT COUNT(DISTINCT u) FROM User u JOIN u.roles r WHERE r.role = :roleType")
    Long countByRolesContaining(@Param("roleType") RoleType roleType);
}
