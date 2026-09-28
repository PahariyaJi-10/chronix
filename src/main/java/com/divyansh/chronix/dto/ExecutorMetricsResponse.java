package com.divyansh.chronix.dto;

public class ExecutorMetricsResponse {

    private int corePoolSize;
    private int maxPoolSize;
    private int queueCapacity;

    private int activeThreads;
    private int poolSize;
    private int queueSize;
    private int availableThreads;

    public ExecutorMetricsResponse(
            int corePoolSize,
            int maxPoolSize,
            int queueCapacity,
            int activeThreads,
            int poolSize,
            int queueSize,
            int availableThreads) {

        this.corePoolSize = corePoolSize;
        this.maxPoolSize = maxPoolSize;
        this.queueCapacity = queueCapacity;
        this.activeThreads = activeThreads;
        this.poolSize = poolSize;
        this.queueSize = queueSize;
        this.availableThreads = availableThreads;
    }

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public int getQueueCapacity() {
        return queueCapacity;
    }

    public int getActiveThreads() {
        return activeThreads;
    }

    public int getPoolSize() {
        return poolSize;
    }

    public int getQueueSize() {
        return queueSize;
    }

    public int getAvailableThreads() {
        return availableThreads;
    }
}