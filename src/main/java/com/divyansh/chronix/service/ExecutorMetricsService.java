package com.divyansh.chronix.service;

import com.divyansh.chronix.dto.ExecutorMetricsResponse;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

@Service
public class ExecutorMetricsService {

    private final ThreadPoolTaskExecutor executor;

    public ExecutorMetricsService(
            ThreadPoolTaskExecutor executor) {

        this.executor = executor;
    }

    public ExecutorMetricsResponse getMetrics() {

        return new ExecutorMetricsResponse(
                executor.getCorePoolSize(),
                executor.getMaxPoolSize(),
                executor.getPoolSize(),
                executor.getActiveCount(),
                executor.getThreadPoolExecutor()
                        .getQueue()
                        .size(),
                executor.getThreadPoolExecutor()
                        .getCompletedTaskCount(),
                executor.getQueueCapacity()
        );
    }
}