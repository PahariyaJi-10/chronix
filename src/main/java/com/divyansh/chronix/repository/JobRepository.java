package com.divyansh.chronix.repository;

import com.divyansh.chronix.entity.Job;
import com.divyansh.chronix.entity.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findByStatusAndScheduledAtLessThanEqual(
            JobStatus status,
            LocalDateTime scheduledAt
    );
}
