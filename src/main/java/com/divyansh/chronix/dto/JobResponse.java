package com.divyansh.chronix.dto;

import com.divyansh.chronix.entity.JobPriority;
import com.divyansh.chronix.entity.JobStatus;
import com.divyansh.chronix.entity.JobType;

public class JobResponse {

    private Long id;
    private String name;
    private JobType type;
    private JobStatus status;
    private JobPriority priority;
    private Long dependsOnJobId;

    public JobResponse() {
    }

    public JobResponse(
            Long id,
            String name,
            JobType type,
            JobStatus status,
            JobPriority priority,
            Long dependsOnJobId) {

        this.id = id;
        this.name = name;
        this.type = type;
        this.status = status;
        this.priority = priority;
        this.dependsOnJobId = dependsOnJobId;
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
}