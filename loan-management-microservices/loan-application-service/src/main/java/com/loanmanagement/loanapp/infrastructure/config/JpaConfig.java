package com.loanmanagement.loanapp.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA Configuration
 * Enables JPA auditing for automatic creation and modification timestamps
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig {
    // JPA auditing configuration
}
