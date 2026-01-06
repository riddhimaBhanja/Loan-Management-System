package com.loanmanagement.loanapp.domain.service;

import com.loanmanagement.common.exception.ResourceNotFoundException;
import com.loanmanagement.loanapp.application.dto.response.DocumentResponse;
import com.loanmanagement.loanapp.application.mapper.DocumentMapper;
import com.loanmanagement.loanapp.domain.enums.DocumentType;
import com.loanmanagement.loanapp.domain.model.Document;
import com.loanmanagement.loanapp.domain.repository.DocumentRepository;
import com.loanmanagement.loanapp.domain.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for Document operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final LoanRepository loanRepository;
    private final FileStorageService fileStorageService;
    private final DocumentMapper documentMapper;

    @Override
    public DocumentResponse uploadDocument(Long loanId, DocumentType documentType, MultipartFile file, Long uploadedBy) throws IOException {
        log.info("Uploading document for loan ID: {}, type: {}", loanId, documentType);

        // Verify loan exists
        if (!loanRepository.existsById(loanId)) {
            throw new ResourceNotFoundException("Loan not found with ID: " + loanId);
        }

        // Store file
        String filePath = fileStorageService.storeFile(file, loanId);

        // Create document entity
        Document document = Document.builder()
                .loanId(loanId)
                .documentType(documentType)
                .fileName(filePath.substring(filePath.lastIndexOf("/") + 1))
                .originalFileName(file.getOriginalFilename())
                .filePath(filePath)
                .fileSize(file.getSize())
                .contentType(file.getContentType())
                .uploadedBy(uploadedBy)
                .build();

        Document savedDocument = documentRepository.save(document);
        log.info("Document uploaded successfully with ID: {}", savedDocument.getId());

        return documentMapper.toResponse(savedDocument);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentResponse> getLoanDocuments(Long loanId) {
        log.debug("Fetching documents for loan ID: {}", loanId);
        return documentRepository.findByLoanId(loanId).stream()
                .map(documentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentResponse getDocument(Long documentId) {
        log.debug("Fetching document with ID: {}", documentId);
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with ID: " + documentId));
        return documentMapper.toResponse(document);
    }

    @Override
    @Transactional(readOnly = true)
    public Resource downloadDocument(Long documentId) throws IOException {
        log.info("Downloading document with ID: {}", documentId);
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with ID: " + documentId));

        return fileStorageService.loadFileAsResource(document.getFilePath());
    }

    @Override
    public void deleteDocument(Long documentId) throws IOException {
        log.info("Deleting document with ID: {}", documentId);
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with ID: " + documentId));

        // Delete file from storage
        fileStorageService.deleteFile(document.getFilePath());

        // Delete document record
        documentRepository.delete(document);
        log.info("Document deleted successfully with ID: {}", documentId);
    }
}
