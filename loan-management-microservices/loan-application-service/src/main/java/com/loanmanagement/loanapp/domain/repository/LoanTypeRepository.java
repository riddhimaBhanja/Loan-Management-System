package com.loanmanagement.loanapp.domain.repository;

import com.loanmanagement.loanapp.domain.model.LoanType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for LoanType entity
 */
@Repository
public interface LoanTypeRepository extends JpaRepository<LoanType, Long> {

    Optional<LoanType> findByName(String name);

    List<LoanType> findByIsActiveTrue();

    boolean existsByName(String name);
}
