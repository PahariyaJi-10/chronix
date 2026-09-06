package com.divyansh.chronix.dto;

import java.time.LocalDateTime;

public class DeadLetterJobResponse {

    private Long id;
    private Long jobId;
    private String jobName;
    private Integer attemptCount;
    private String errorMessage;
    private String payload;
    private LocalDateTime failedAt;

    public DeadLetterJobResponse() {
    }

    public DeadLetterJobResponse(
            Long id,
            Long jobId,
            String jobName,
            Integer attemptCount,
            String errorMessage,
            String payload,
            LocalDateTime failedAt) {

        this.id = id;
        this.jobId = jobId;
        this.jobName = jobName;
        this.attemptCount = attemptCount;
        this.errorMessage = errorMessage;
        this.payload = payload;
        this.failedAt = failedAt;
    }

    public Long getId() {
        return id;
    }

    public Long getJobId() {
        return jobId;
    }

    public String getJobName() {
        return jobName;
    }

    public Integer getAttemptCount() {
        return attemptCount;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getPayload() {
        return payload;
    }

    public LocalDateTime getFailedAt() {
        return failedAt;
    }
}