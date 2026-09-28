package com.divyansh.chronix.service;

import com.divyansh.chronix.dto.ExecutorMetricsResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

@Service
public class ExecutorMetricsService {

    private final ThreadPoolTaskExecutor taskExecutor;

    public ExecutorMetricsService(
            @Qualifier("chronixTaskExecutor")
            ThreadPoolTaskExecutor taskExecutor) {

        this.taskExecutor = taskExecutor;
    }

    public ExecutorMetricsResponse getExecutorMetrics() {

        return new ExecutorMetricsResponse(
                taskExecutor.getCorePoolSize(),
                taskExecutor.getMaxPoolSize(),

                taskExecutor.getThreadPoolExecutor()
                        .getQueue()
                        .remainingCapacity()
                        + taskExecutor.getThreadPoolExecutor()
                                .getQueue()
                                .size(),

                taskExecutor.getActiveCount(),
                taskExecutor.getPoolSize(),

                taskExecutor.getThreadPoolExecutor()
                        .getQueue()
                        .size(),

                taskExecutor.getMaxPoolSize()
                        - taskExecutor.getActiveCount()
        );
    }
}