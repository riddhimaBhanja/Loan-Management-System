package com.loanmanagement.notification.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Configuration for async email sending
 */
@Configuration
public class AsyncConfig {

    @Value("${notification.async.core-pool-size:5}")
    private int corePoolSize;

    @Value("${notification.async.max-pool-size:10}")
    private int maxPoolSize;

    @Value("${notification.async.queue-capacity:100}")
    private int queueCapacity;

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("NotificationAsync-");
        executor.initialize();
        return executor;
    }
}
