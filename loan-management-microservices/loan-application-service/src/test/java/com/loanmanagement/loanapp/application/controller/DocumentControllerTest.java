package com.loanmanagement.loanapp.application.controller;

import com.loanmanagement.common.dto.ApiResponse;
import com.loanmanagement.loanapp.application.dto.response.DocumentResponse;
import com.loanmanagement.loanapp.domain.enums.DocumentType;
import com.loanmanagement.loanapp.domain.service.DocumentService;
import com.loanmanagement.loanapp.infrastructure.security.UserPrincipal;
import com.loanmanagement.loanapp.shared.constants.MessageConstants;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = DocumentController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration.class,
        org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class,
        com.loanmanagement.loanapp.infrastructure.config.JpaConfig.class
})
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class DocumentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DocumentService documentService;

    @MockBean
    private com.loanmanagement.loanapp.infrastructure.security.JwtUtil jwtUtil;

    @MockBean
    private com.loanmanagement.loanapp.infrastructure.security.JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void uploadDocument_success() throws Exception {
        DocumentResponse response = new DocumentResponse();
        Mockito.when(documentService.uploadDocument(anyLong(), any(), any(MultipartFile.class), anyLong()))
                .thenReturn(response);

        UserPrincipal userPrincipal = Mockito.mock(UserPrincipal.class);
        Mockito.when(userPrincipal.getUserId()).thenReturn(1L);
        Authentication authentication =
                new TestingAuthenticationToken(userPrincipal, null, "ROLE_CUSTOMER");

        mockMvc.perform(multipart("/api/documents/upload")
                        .file("file", "test".getBytes())
                        .param("loanId", "1")
                        .param("documentType", DocumentType.ID_PROOF.name())
                        .principal(authentication))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value(MessageConstants.DOCUMENT_UPLOADED))
                .andExpect(jsonPath("$.data", notNullValue()));
    }

    @Test
    void getLoanDocuments_success() throws Exception {
        Mockito.when(documentService.getLoanDocuments(1L))
                .thenReturn(List.of(new DocumentResponse()));

        mockMvc.perform(get("/api/documents/loan/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(MessageConstants.DOCUMENTS_FETCHED));
    }

    @Test
    void getDocument_success() throws Exception {
        Mockito.when(documentService.getDocument(1L))
                .thenReturn(new DocumentResponse());

        mockMvc.perform(get("/api/documents/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(MessageConstants.DOCUMENTS_FETCHED));
    }

    @Test
    void downloadDocument_success() throws Exception {
        DocumentResponse documentResponse = new DocumentResponse();
        documentResponse.setContentType(MediaType.APPLICATION_PDF_VALUE);
        documentResponse.setOriginalFileName("file.pdf");

        Resource resource = new ByteArrayResource("data".getBytes());

        Mockito.when(documentService.getDocument(1L)).thenReturn(documentResponse);
        Mockito.when(documentService.downloadDocument(1L)).thenReturn(resource);

        mockMvc.perform(get("/api/documents/download/1"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"file.pdf\""))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));
    }

    @Test
    void deleteDocument_success() throws Exception {
        Mockito.doNothing().when(documentService).deleteDocument(1L);

        mockMvc.perform(delete("/api/documents/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(MessageConstants.DOCUMENT_DELETED));
    }
}
