package com.divyansh.chronix.repository;

import com.divyansh.chronix.entity.JobExecution;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobExecutionRepository
        extends JpaRepository<JobExecution, Long> {

    List<JobExecution> findByJobIdOrderByStartedAtDesc(Long jobId);
}