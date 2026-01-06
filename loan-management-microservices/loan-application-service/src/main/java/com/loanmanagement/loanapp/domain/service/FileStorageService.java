package com.loanmanagement.loanapp.domain.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Service for file storage operations
 */
public interface FileStorageService {

    String storeFile(MultipartFile file, Long loanId) throws IOException;

    Resource loadFileAsResource(String fileName) throws IOException;

    void deleteFile(String fileName) throws IOException;

    Path getFilePath(String fileName);

    void validateFile(MultipartFile file);
}
