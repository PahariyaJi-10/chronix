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
            String cronExpression) {

        this.id = id;
        this.name = name;
        this.type = type;
        this.status = status;
        this.priority = priority;
        this.dependsOnJobId = dependsOnJobId;
        this.scheduleType = scheduleType;
        this.cronExpression = cronExpression;
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
}