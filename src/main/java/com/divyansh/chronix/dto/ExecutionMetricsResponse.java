package com.divyansh.chronix.dto;

public class ExecutionMetricsResponse {

    private Long jobId;

    private long totalExecutions;
    private long successfulExecutions;
    private long failedExecutions;
    private long runningExecutions;

    private double successRate;
    private double failureRate;

    private long totalExecutionTimeMs;
    private double averageExecutionTimeMs;

    public ExecutionMetricsResponse() {
    }

    // Constructor for global metrics
    public ExecutionMetricsResponse(
            long totalExecutions,
            long successfulExecutions,
            long failedExecutions,
            long runningExecutions,
            double successRate,
            double failureRate,
            long totalExecutionTimeMs,
            double averageExecutionTimeMs) {

        this.totalExecutions = totalExecutions;
        this.successfulExecutions = successfulExecutions;
        this.failedExecutions = failedExecutions;
        this.runningExecutions = runningExecutions;
        this.successRate = successRate;
        this.failureRate = failureRate;
        this.totalExecutionTimeMs = totalExecutionTimeMs;
        this.averageExecutionTimeMs = averageExecutionTimeMs;
    }

    // Constructor for per-job metrics
    public ExecutionMetricsResponse(
            Long jobId,
            long totalExecutions,
            long successfulExecutions,
            long failedExecutions,
            long runningExecutions,
            double successRate,
            double failureRate,
            long totalExecutionTimeMs,
            double averageExecutionTimeMs) {

        this.jobId = jobId;
        this.totalExecutions = totalExecutions;
        this.successfulExecutions = successfulExecutions;
        this.failedExecutions = failedExecutions;
        this.runningExecutions = runningExecutions;
        this.successRate = successRate;
        this.failureRate = failureRate;
        this.totalExecutionTimeMs = totalExecutionTimeMs;
        this.averageExecutionTimeMs = averageExecutionTimeMs;
    }

    public Long getJobId() {
        return jobId;
    }

    public long getTotalExecutions() {
        return totalExecutions;
    }

    public long getSuccessfulExecutions() {
        return successfulExecutions;
    }

    public long getFailedExecutions() {
        return failedExecutions;
    }

    public long getRunningExecutions() {
        return runningExecutions;
    }

    public double getSuccessRate() {
        return successRate;
    }

    public double getFailureRate() {
        return failureRate;
    }

    public long getTotalExecutionTimeMs() {
        return totalExecutionTimeMs;
    }

    public double getAverageExecutionTimeMs() {
        return averageExecutionTimeMs;
    }
}