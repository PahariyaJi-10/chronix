package com.divyansh.chronix.dto;

public class ExecutorMetricsResponse {

    private int corePoolSize;
    private int maxPoolSize;
    private int currentPoolSize;
    private int activeWorkerCount;
    private int queuedTasks;
    private long completedTasks;
    private int queueCapacity;

    public ExecutorMetricsResponse() {
    }

    public ExecutorMetricsResponse(
            int corePoolSize,
            int maxPoolSize,
            int currentPoolSize,
            int activeWorkerCount,
            int queuedTasks,
            long completedTasks,
            int queueCapacity) {

        this.corePoolSize = corePoolSize;
        this.maxPoolSize = maxPoolSize;
        this.currentPoolSize = currentPoolSize;
        this.activeWorkerCount = activeWorkerCount;
        this.queuedTasks = queuedTasks;
        this.completedTasks = completedTasks;
        this.queueCapacity = queueCapacity;
    }

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public int getCurrentPoolSize() {
        return currentPoolSize;
    }

    public int getActiveWorkerCount() {
        return activeWorkerCount;
    }

    public int getQueuedTasks() {
        return queuedTasks;
    }

    public long getCompletedTasks() {
        return completedTasks;
    }

    public int getQueueCapacity() {
        return queueCapacity;
    }
}