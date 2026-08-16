package com.divyansh.chronix.repository;

import com.divyansh.chronix.entity.JobExecution;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobExecutionRepository
        extends JpaRepository<JobExecution, Long> {

    // Get executions for a specific job
    List<JobExecution> findByJobIdOrderByStartedAtDesc(Long jobId);

    // Get all executions, newest first
    List<JobExecution> findAllByOrderByStartedAtDesc();
}