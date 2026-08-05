package com.divyansh.chronix.dto;

import com.divyansh.chronix.entity.JobType;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class CreateJobRequest {

    @NotBlank(message = "Job name is required")
    private String name;

    @NotNull(message = "Job type is required")
    private JobType type;

    @NotNull(message = "Scheduled time is required")
    @FutureOrPresent(message = "Scheduled time must be in the present or future")
    private LocalDateTime scheduledAt;

    @NotBlank(message = "Payload is required")
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