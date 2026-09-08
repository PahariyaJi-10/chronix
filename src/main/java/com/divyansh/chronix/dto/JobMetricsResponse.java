package com.divyansh.chronix.dto;

public class JobMetricsResponse {

    private long totalJobs;
    private long pendingJobs;
    private long runningJobs;
    private long completedJobs;
    private long failedJobs;
    private long cancelledJobs;
    private long pausedJobs;

    public JobMetricsResponse() {
    }

    public JobMetricsResponse(
            long totalJobs,
            long pendingJobs,
            long runningJobs,
            long completedJobs,
            long failedJobs,
            long cancelledJobs,
            long pausedJobs) {

        this.totalJobs = totalJobs;
        this.pendingJobs = pendingJobs;
        this.runningJobs = runningJobs;
        this.completedJobs = completedJobs;
        this.failedJobs = failedJobs;
        this.cancelledJobs = cancelledJobs;
        this.pausedJobs = pausedJobs;
    }

    public long getTotalJobs() {
        return totalJobs;
    }

    public long getPendingJobs() {
        return pendingJobs;
    }

    public long getRunningJobs() {
        return runningJobs;
    }

    public long getCompletedJobs() {
        return completedJobs;
    }

    public long getFailedJobs() {
        return failedJobs;
    }

    public long getCancelledJobs() {
        return cancelledJobs;
    }

    public long getPausedJobs() {
        return pausedJobs;
    }
}