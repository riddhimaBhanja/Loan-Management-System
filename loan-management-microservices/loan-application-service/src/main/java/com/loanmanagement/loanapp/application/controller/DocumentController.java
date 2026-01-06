package com.loanmanagement.loanapp.application.controller;

import com.loanmanagement.common.dto.ApiResponse;
import com.loanmanagement.loanapp.application.dto.response.DocumentResponse;
import com.loanmanagement.loanapp.domain.enums.DocumentType;
import com.loanmanagement.loanapp.domain.service.DocumentService;
import com.loanmanagement.loanapp.shared.constants.MessageConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * REST controller for Document operations
 */
@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
@Slf4j
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping("/upload")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<ApiResponse> uploadDocument(
            @RequestParam("loanId") Long loanId,
            @RequestParam("documentType") DocumentType documentType,
            @RequestParam("file") MultipartFile file,
            Authentication authentication) throws IOException {

        com.loanmanagement.loanapp.infrastructure.security.UserPrincipal userPrincipal =
                (com.loanmanagement.loanapp.infrastructure.security.UserPrincipal) authentication.getPrincipal();
        Long uploadedBy = userPrincipal.getUserId();
        log.info("Uploading document for loan ID: {}, type: {}", loanId, documentType);

        DocumentResponse response = documentService.uploadDocument(loanId, documentType, file, uploadedBy);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(MessageConstants.DOCUMENT_UPLOADED, response));
    }

    @GetMapping("/loan/{loanId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'LOAN_OFFICER', 'ADMIN')")
    public ResponseEntity<ApiResponse> getLoanDocuments(@PathVariable Long loanId) {
        log.info("Fetching documents for loan ID: {}", loanId);
        List<DocumentResponse> responses = documentService.getLoanDocuments(loanId);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.DOCUMENTS_FETCHED, responses));
    }

    @GetMapping("/{documentId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'LOAN_OFFICER', 'ADMIN')")
    public ResponseEntity<ApiResponse> getDocument(@PathVariable Long documentId) {
        log.info("Fetching document with ID: {}", documentId);
        DocumentResponse response = documentService.getDocument(documentId);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.DOCUMENTS_FETCHED, response));
    }

    @GetMapping("/download/{documentId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'LOAN_OFFICER', 'ADMIN')")
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long documentId) throws IOException {
        log.info("Downloading document with ID: {}", documentId);
        DocumentResponse documentInfo = documentService.getDocument(documentId);
        Resource resource = documentService.downloadDocument(documentId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(documentInfo.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + documentInfo.getOriginalFileName() + "\"")
                .body(resource);
    }

    @DeleteMapping("/{documentId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<ApiResponse> deleteDocument(@PathVariable Long documentId) throws IOException {
        log.info("Deleting document with ID: {}", documentId);
        documentService.deleteDocument(documentId);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.DOCUMENT_DELETED, null));
    }
}
