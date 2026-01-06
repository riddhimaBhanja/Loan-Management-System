package com.loanmanagement.loanapp.domain.service;

import com.loanmanagement.common.exception.ResourceNotFoundException;
import com.loanmanagement.loanapp.application.dto.response.DocumentResponse;
import com.loanmanagement.loanapp.application.mapper.DocumentMapper;
import com.loanmanagement.loanapp.domain.enums.DocumentType;
import com.loanmanagement.loanapp.domain.model.Document;
import com.loanmanagement.loanapp.domain.repository.DocumentRepository;
import com.loanmanagement.loanapp.domain.repository.LoanRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceImplTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private DocumentMapper documentMapper;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private DocumentServiceImpl documentService;

    private Document createDocument(Long id) {
        return Document.builder()
                .id(id)
                .loanId(1L)
                .documentType(DocumentType.ID_PROOF)
                .fileName("file.pdf")
                .originalFileName("file.pdf")
                .filePath("/files/file.pdf")
                .fileSize(100L)
                .contentType("application/pdf")
                .uploadedBy(10L)
                .build();
    }

    @Test
    void uploadDocument_success() throws Exception {
        when(loanRepository.existsById(1L)).thenReturn(true);
        when(fileStorageService.storeFile(any(), eq(1L))).thenReturn("/files/file.pdf");
        when(multipartFile.getOriginalFilename()).thenReturn("file.pdf");
        when(multipartFile.getSize()).thenReturn(100L);
        when(multipartFile.getContentType()).thenReturn("application/pdf");

        Document saved = createDocument(1L);
        when(documentRepository.save(any(Document.class))).thenReturn(saved);
        when(documentMapper.toResponse(saved)).thenReturn(new DocumentResponse());

        DocumentResponse response =
                documentService.uploadDocument(1L, DocumentType.ID_PROOF, multipartFile, 10L);

        assertNotNull(response);
        verify(documentRepository).save(any(Document.class));
    }

    @Test
    void uploadDocument_shouldThrowException_whenLoanNotFound() {
        when(loanRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> documentService.uploadDocument(1L, DocumentType.ID_PROOF, multipartFile, 10L));
    }

    @Test
    void getLoanDocuments_success() {
        when(documentRepository.findByLoanId(1L))
                .thenReturn(List.of(createDocument(1L)));
        when(documentMapper.toResponse(any(Document.class)))
                .thenReturn(new DocumentResponse());

        List<DocumentResponse> responses =
                documentService.getLoanDocuments(1L);

        assertEquals(1, responses.size());
    }

    @Test
    void getDocument_success() {
        Document document = createDocument(1L);

        when(documentRepository.findById(1L))
                .thenReturn(Optional.of(document));
        when(documentMapper.toResponse(document))
                .thenReturn(new DocumentResponse());

        DocumentResponse response = documentService.getDocument(1L);

        assertNotNull(response);
    }

    @Test
    void getDocument_shouldThrowException_whenNotFound() {
        when(documentRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> documentService.getDocument(1L));
    }

    @Test
    void downloadDocument_success() throws IOException {
        Document document = createDocument(1L);
        Resource resource = new ByteArrayResource("data".getBytes());

        when(documentRepository.findById(1L))
                .thenReturn(Optional.of(document));
        when(fileStorageService.loadFileAsResource("/files/file.pdf"))
                .thenReturn(resource);

        Resource result = documentService.downloadDocument(1L);

        assertNotNull(result);
    }

    @Test
    void deleteDocument_success() throws IOException {
        Document document = createDocument(1L);

        when(documentRepository.findById(1L))
                .thenReturn(Optional.of(document));

        documentService.deleteDocument(1L);

        verify(fileStorageService).deleteFile("/files/file.pdf");
        verify(documentRepository).delete(document);
    }

    @Test
    void deleteDocument_shouldThrowException_whenNotFound() {
        when(documentRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> documentService.deleteDocument(1L));
    }
}
