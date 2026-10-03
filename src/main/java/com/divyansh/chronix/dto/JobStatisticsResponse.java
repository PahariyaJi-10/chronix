package com.divyansh.chronix.dto;

public class JobStatisticsResponse {

    private Long jobId;
    private String jobName;

    private long totalExecutions;
    private long successfulExecutions;
    private long failedExecutions;

    private double successRate;
    private double failureRate;

    public JobStatisticsResponse(
            Long jobId,
            String jobName,
            long totalExecutions,
            long successfulExecutions,
            long failedExecutions,
            double successRate,
            double failureRate) {

        this.jobId = jobId;
        this.jobName = jobName;
        this.totalExecutions = totalExecutions;
        this.successfulExecutions = successfulExecutions;
        this.failedExecutions = failedExecutions;
        this.successRate = successRate;
        this.failureRate = failureRate;
    }

    public Long getJobId() {
        return jobId;
    }

    public String getJobName() {
        return jobName;
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

    public double getSuccessRate() {
        return successRate;
    }

    public double getFailureRate() {
        return failureRate;
    }
}