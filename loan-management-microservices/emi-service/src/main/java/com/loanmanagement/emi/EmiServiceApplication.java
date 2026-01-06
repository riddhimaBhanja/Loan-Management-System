package com.loanmanagement.emi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * EMI & Repayment Service - Main Application
 *
 * Handles:
 * - EMI schedule generation
 * - EMI payment tracking
 * - Outstanding balance calculation
 * - Payment status management
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableJpaAuditing
@EnableScheduling
public class EmiServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmiServiceApplication.class, args);
    }
}
