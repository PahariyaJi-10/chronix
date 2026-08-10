package com.divyansh.chronix.dto;

import com.divyansh.chronix.entity.JobStatus;

import java.time.LocalDateTime;

public class JobExecutionResponse {

    private Long id;
    private Long jobId;
    private JobStatus status;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private Integer attemptNumber;
    private String errorMessage;

    public JobExecutionResponse() {
    }

    public JobExecutionResponse(
            Long id,
            Long jobId,
            JobStatus status,
            LocalDateTime startedAt,
            LocalDateTime finishedAt,
            Integer attemptNumber,
            String errorMessage) {

        this.id = id;
        this.jobId = jobId;
        this.status = status;
        this.startedAt = startedAt;
        this.finishedAt = finishedAt;
        this.attemptNumber = attemptNumber;
        this.errorMessage = errorMessage;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(LocalDateTime finishedAt) {
        this.finishedAt = finishedAt;
    }

    public Integer getAttemptNumber() {
        return attemptNumber;
    }

    public void setAttemptNumber(Integer attemptNumber) {
        this.attemptNumber = attemptNumber;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}