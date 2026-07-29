package com.divyansh.chronix.dto;

import com.divyansh.chronix.entity.JobStatus;
import com.divyansh.chronix.entity.JobType;

public class JobResponse {

    private Long id;
    private String name;
    private JobType type;
    private JobStatus status;

    public JobResponse() {
    }

    public JobResponse(Long id, String name, JobType type, JobStatus status) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.status = status;
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
}