package com.loanmanagement.loanapp.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for file storage
 */
@Configuration
@ConfigurationProperties(prefix = "file")
@Data
public class FileStorageConfig {

    private String uploadDir = "./uploads/loan-documents";
    private long maxFileSize = 10485760; // 10MB in bytes
    private String[] allowedExtensions = {"pdf", "jpg", "jpeg", "png", "doc", "docx"};
}
