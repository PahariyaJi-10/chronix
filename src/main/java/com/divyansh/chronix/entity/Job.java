package com.divyansh.chronix.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "jobs")
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private JobType type;

    @Enumerated(EnumType.STRING)
    private JobStatus status;

    private LocalDateTime scheduledAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Integer retryCount;

    @Column(columnDefinition = "TEXT")
    private String payload;

    public Job() {
    }

    // Getters and Setters will be added next
}