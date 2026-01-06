package com.loanmanagement.loanapp.domain.service;

import com.loanmanagement.common.exception.ResourceNotFoundException;
import com.loanmanagement.loanapp.infrastructure.config.FileStorageConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class FileStorageServiceImplTest {

    @TempDir
    Path tempDir;

    private FileStorageServiceImpl fileStorageService;
    private FileStorageConfig fileStorageConfig;

    @BeforeEach
    void setUp() {
        fileStorageConfig = Mockito.mock(FileStorageConfig.class);
        when(fileStorageConfig.getUploadDir()).thenReturn(tempDir.toString());
        when(fileStorageConfig.getMaxFileSize()).thenReturn(5 * 1024 * 1024L);
        when(fileStorageConfig.getAllowedExtensions()).thenReturn(new String[]{"pdf", "jpg", "png"});

        fileStorageService = new FileStorageServiceImpl(fileStorageConfig);
    }

    @Test
    void storeFile_success() throws Exception {
        MultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "content".getBytes()
        );

        String path = fileStorageService.storeFile(file, 1L);

        assertNotNull(path);
        assertTrue(path.startsWith("1/"));
    }

    @Test
    void storeFile_shouldFailForEmptyFile() {
        MultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                new byte[0]
        );

        assertThrows(IllegalArgumentException.class,
                () -> fileStorageService.storeFile(file, 1L));
    }

    @Test
    void loadFileAsResource_success() throws Exception {
        MultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "data".getBytes()
        );

        String storedPath = fileStorageService.storeFile(file, 2L);

        Resource resource = fileStorageService.loadFileAsResource(storedPath);

        assertNotNull(resource);
        assertTrue(resource.exists());
    }

    @Test
    void loadFileAsResource_shouldThrowException_whenFileNotFound() {
        assertThrows(ResourceNotFoundException.class,
                () -> fileStorageService.loadFileAsResource("missing/file.pdf"));
    }

    @Test
    void deleteFile_success() throws Exception {
        MultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "delete".getBytes()
        );

        String storedPath = fileStorageService.storeFile(file, 3L);

        fileStorageService.deleteFile(storedPath);

        assertFalse(fileStorageService.getFilePath(storedPath).toFile().exists());
    }

    @Test
    void getFilePath_shouldReturnResolvedPath() {
        Path path = fileStorageService.getFilePath("sample/file.pdf");

        assertNotNull(path);
        assertTrue(path.toString().contains("sample"));
    }

    @Test
    void validateFile_shouldFailForInvalidExtension() {
        MultipartFile file = new MockMultipartFile(
                "file",
                "test.exe",
                "application/octet-stream",
                "data".getBytes()
        );

        assertThrows(IllegalArgumentException.class,
                () -> fileStorageService.validateFile(file));
    }
}
