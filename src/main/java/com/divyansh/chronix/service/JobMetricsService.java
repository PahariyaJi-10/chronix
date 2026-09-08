package com.divyansh.chronix.service;

import com.divyansh.chronix.dto.JobMetricsResponse;
import com.divyansh.chronix.entity.JobStatus;
import com.divyansh.chronix.repository.JobRepository;
import org.springframework.stereotype.Service;

@Service
public class JobMetricsService {

    private final JobRepository jobRepository;

    public JobMetricsService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public JobMetricsResponse getMetrics() {

        long totalJobs = jobRepository.count();

        long pendingJobs =
                jobRepository.countByStatus(JobStatus.PENDING);

        long runningJobs =
                jobRepository.countByStatus(JobStatus.RUNNING);

        long completedJobs =
                jobRepository.countByStatus(JobStatus.COMPLETED);

        long failedJobs =
                jobRepository.countByStatus(JobStatus.FAILED);

        long cancelledJobs =
                jobRepository.countByStatus(JobStatus.CANCELLED);

        long pausedJobs =
                jobRepository.countByStatus(JobStatus.PAUSED);

        return new JobMetricsResponse(
                totalJobs,
                pendingJobs,
                runningJobs,
                completedJobs,
                failedJobs,
                cancelledJobs,
                pausedJobs
        );
    }
}