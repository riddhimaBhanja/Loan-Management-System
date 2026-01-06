package com.loanmanagement.loanapp.domain.repository;

import com.loanmanagement.loanapp.domain.enums.DocumentType;
import com.loanmanagement.loanapp.domain.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Document entity
 */
@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findByLoanId(Long loanId);

    List<Document> findByLoanIdAndDocumentType(Long loanId, DocumentType documentType);

    Optional<Document> findByLoanIdAndDocumentTypeAndFileName(Long loanId, DocumentType documentType, String fileName);

    long countByLoanId(Long loanId);
}
