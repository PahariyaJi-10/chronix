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

    public JobResponse() {
    }

    public JobResponse(
            Long id,
            String name,
            JobType type,
            JobStatus status,
            JobPriority priority) {

        this.id = id;
        this.name = name;
        this.type = type;
        this.status = status;
        this.priority = priority;
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
}