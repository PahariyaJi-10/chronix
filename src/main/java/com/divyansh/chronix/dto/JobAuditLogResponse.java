package com.divyansh.chronix.dto;

import com.divyansh.chronix.entity.JobAuditAction;

import java.time.LocalDateTime;

public class JobAuditLogResponse {

    private Long id;
    private Long jobId;
    private String jobName;
    private JobAuditAction action;
    private String message;
    private LocalDateTime createdAt;

    public JobAuditLogResponse() {
    }

    public JobAuditLogResponse(
            Long id,
            Long jobId,
            String jobName,
            JobAuditAction action,
            String message,
            LocalDateTime createdAt) {

        this.id = id;
        this.jobId = jobId;
        this.jobName = jobName;
        this.action = action;
        this.message = message;
        this.createdAt = createdAt;
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

    public JobAuditAction getAction() {
        return action;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}