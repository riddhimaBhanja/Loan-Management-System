package com.loanmanagement.loanapp.domain.service;

import com.loanmanagement.loanapp.application.dto.response.DocumentResponse;
import com.loanmanagement.loanapp.domain.enums.DocumentType;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Service interface for Document operations
 */
public interface DocumentService {

    DocumentResponse uploadDocument(Long loanId, DocumentType documentType, MultipartFile file, Long uploadedBy) throws IOException;

    List<DocumentResponse> getLoanDocuments(Long loanId);

    DocumentResponse getDocument(Long documentId);

    Resource downloadDocument(Long documentId) throws IOException;

    void deleteDocument(Long documentId) throws IOException;
}
