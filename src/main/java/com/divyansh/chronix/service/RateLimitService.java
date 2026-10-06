package com.divyansh.chronix.service;

import com.divyansh.chronix.entity.Job;
import com.divyansh.chronix.entity.JobExecution;
import com.divyansh.chronix.repository.JobExecutionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RateLimitService {

    private final JobExecutionRepository jobExecutionRepository;

    public RateLimitService(
            JobExecutionRepository jobExecutionRepository) {
        this.jobExecutionRepository = jobExecutionRepository;
    }

    public boolean isRateLimitExceeded(Job job) {

        // No rate limit configured
        if (job.getRateLimit() == null
                || job.getRateLimitWindowSeconds() == null) {
            return false;
        }

        LocalDateTime windowStart =
                LocalDateTime.now()
                        .minusSeconds(job.getRateLimitWindowSeconds());

        List<JobExecution> recentExecutions =
                jobExecutionRepository
                        .findByJobIdAndStartedAtAfter(
                                job.getId(),
                                windowStart
                        );

        return recentExecutions.size() >= job.getRateLimit();
    }
}