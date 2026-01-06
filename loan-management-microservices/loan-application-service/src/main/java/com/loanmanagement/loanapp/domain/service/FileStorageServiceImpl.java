package com.loanmanagement.loanapp.domain.service;

import com.loanmanagement.common.exception.ResourceNotFoundException;
import com.loanmanagement.loanapp.infrastructure.config.FileStorageConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.UUID;

/**
 * Implementation of FileStorageService
 */
@Service
@Slf4j
public class FileStorageServiceImpl implements FileStorageService {

    private final Path fileStorageLocation;
    private final FileStorageConfig fileStorageConfig;

    public FileStorageServiceImpl(FileStorageConfig fileStorageConfig) {
        this.fileStorageConfig = fileStorageConfig;
        this.fileStorageLocation = Paths.get(fileStorageConfig.getUploadDir())
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
            log.info("File storage location created: {}", this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    @Override
    public String storeFile(MultipartFile file, Long loanId) throws IOException {
        validateFile(file);

        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = getFileExtension(originalFileName);
        String fileName = loanId + "_" + UUID.randomUUID().toString() + "." + fileExtension;

        try {
            if (fileName.contains("..")) {
                throw new IllegalArgumentException("Filename contains invalid path sequence " + fileName);
            }

            Path loanDirectory = this.fileStorageLocation.resolve(String.valueOf(loanId));
            Files.createDirectories(loanDirectory);

            Path targetLocation = loanDirectory.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            log.info("File stored successfully: {}", fileName);
            return loanId + "/" + fileName;

        } catch (IOException ex) {
            log.error("Could not store file {}. Error: {}", fileName, ex.getMessage());
            throw new IOException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    @Override
    public Resource loadFileAsResource(String fileName) throws IOException {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("File not found: " + fileName);
            }
        } catch (Exception ex) {
            throw new ResourceNotFoundException("File not found: " + fileName);
        }
    }

    @Override
    public void deleteFile(String fileName) throws IOException {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Files.deleteIfExists(filePath);
            log.info("File deleted successfully: {}", fileName);
        } catch (IOException ex) {
            log.error("Could not delete file {}. Error: {}", fileName, ex.getMessage());
            throw new IOException("Could not delete file " + fileName, ex);
        }
    }

    @Override
    public Path getFilePath(String fileName) {
        return this.fileStorageLocation.resolve(fileName).normalize();
    }

    @Override
    public void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Cannot upload empty file");
        }

        if (file.getSize() > fileStorageConfig.getMaxFileSize()) {
            throw new IllegalArgumentException(
                    String.format("File size exceeds maximum limit of %d MB",
                            fileStorageConfig.getMaxFileSize() / (1024 * 1024))
            );
        }

        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || originalFileName.isEmpty()) {
            throw new IllegalArgumentException("File must have a valid name");
        }

        String fileExtension = getFileExtension(originalFileName).toLowerCase();
        if (!Arrays.asList(fileStorageConfig.getAllowedExtensions()).contains(fileExtension)) {
            throw new IllegalArgumentException(
                    String.format("File type not allowed. Allowed types: %s",
                            String.join(", ", fileStorageConfig.getAllowedExtensions()))
            );
        }

        log.info("File validation successful: {}", originalFileName);
    }

    private String getFileExtension(String fileName) {
        int lastIndexOf = fileName.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return "";
        }
        return fileName.substring(lastIndexOf + 1);
    }
}
