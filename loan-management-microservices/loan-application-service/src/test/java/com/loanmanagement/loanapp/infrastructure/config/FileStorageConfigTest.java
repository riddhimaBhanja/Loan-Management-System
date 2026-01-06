package com.loanmanagement.loanapp.infrastructure.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FileStorageConfigTest {

    @Test
    void defaultValues_shouldBeSetCorrectly() {
        FileStorageConfig config = new FileStorageConfig();

        assertEquals("./uploads/loan-documents", config.getUploadDir());
        assertEquals(10485760L, config.getMaxFileSize());
        assertArrayEquals(
                new String[]{"pdf", "jpg", "jpeg", "png", "doc", "docx"},
                config.getAllowedExtensions()
        );
    }

    @Test
    void settersAndGetters_shouldWorkCorrectly() {
        FileStorageConfig config = new FileStorageConfig();

        config.setUploadDir("/tmp/uploads");
        config.setMaxFileSize(20971520L);
        config.setAllowedExtensions(new String[]{"txt"});

        assertEquals("/tmp/uploads", config.getUploadDir());
        assertEquals(20971520L, config.getMaxFileSize());
        assertArrayEquals(new String[]{"txt"}, config.getAllowedExtensions());
    }
}
