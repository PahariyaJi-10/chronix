package com.divyansh.chronix.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Value("${chronix.executor.core-pool-size}")
    private int corePoolSize;

    @Value("${chronix.executor.max-pool-size}")
    private int maxPoolSize;

    @Value("${chronix.executor.queue-capacity}")
    private int queueCapacity;

    @Bean(name = "chronixTaskExecutor")
    public ThreadPoolTaskExecutor chronixTaskExecutor() {

        ThreadPoolTaskExecutor executor =
                new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);

        executor.setThreadNamePrefix("chronix-worker-");

        // Graceful shutdown configuration
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);

        executor.initialize();

        return executor;
    }
}