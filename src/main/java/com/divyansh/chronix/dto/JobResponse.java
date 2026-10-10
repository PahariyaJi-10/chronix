package com.divyansh.chronix.dto;

import com.divyansh.chronix.entity.JobPriority;
import com.divyansh.chronix.entity.JobStatus;
import com.divyansh.chronix.entity.JobType;
import com.divyansh.chronix.entity.ScheduleType;

public class JobResponse {

    private Long id;
    private String name;
    private JobType type;
    private JobStatus status;
    private JobPriority priority;
    private Long dependsOnJobId;
    private ScheduleType scheduleType;
    private String cronExpression;
    private String tags;

    // Rate limiting
    private Integer rateLimit;
    private Integer rateLimitWindowSeconds;

    // Concurrency control
    private Integer maxConcurrentExecutions;

    public JobResponse() {
    }

    public JobResponse(
            Long id,
            String name,
            JobType type,
            JobStatus status,
            JobPriority priority,
            Long dependsOnJobId,
            ScheduleType scheduleType,
            String cronExpression,
            String tags,
            Integer rateLimit,
            Integer rateLimitWindowSeconds,
            Integer maxConcurrentExecutions) {

        this.id = id;
        this.name = name;
        this.type = type;
        this.status = status;
        this.priority = priority;
        this.dependsOnJobId = dependsOnJobId;
        this.scheduleType = scheduleType;
        this.cronExpression = cronExpression;
        this.tags = tags;
        this.rateLimit = rateLimit;
        this.rateLimitWindowSeconds = rateLimitWindowSeconds;
        this.maxConcurrentExecutions = maxConcurrentExecutions;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public JobType getType() {
        return type;
    }

    public JobStatus getStatus() {
        return status;
    }

    public JobPriority getPriority() {
        return priority;
    }

    public Long getDependsOnJobId() {
        return dependsOnJobId;
    }

    public ScheduleType getScheduleType() {
        return scheduleType;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public String getTags() {
        return tags;
    }

    // Rate limiting getters

    public Integer getRateLimit() {
        return rateLimit;
    }

    public Integer getRateLimitWindowSeconds() {
        return rateLimitWindowSeconds;
    }

    // Concurrency control getter

    public Integer getMaxConcurrentExecutions() {
        return maxConcurrentExecutions;
    }
}