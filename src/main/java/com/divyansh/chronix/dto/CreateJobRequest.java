package com.divyansh.chronix.dto;

import com.divyansh.chronix.entity.JobType;
import java.time.LocalDateTime;

public class CreateJobRequest {

    private String name;
    private JobType type;
    private LocalDateTime scheduledAt;
    private String payload;

    public CreateJobRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public JobType getType() {
        return type;
    }

    public void setType(JobType type) {
        this.type = type;
    }

    public LocalDateTime getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(LocalDateTime scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}