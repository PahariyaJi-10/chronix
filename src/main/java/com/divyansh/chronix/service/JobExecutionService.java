package com.divyansh.chronix.service;

import com.divyansh.chronix.dto.JobExecutionResponse;
import com.divyansh.chronix.entity.JobExecution;
import com.divyansh.chronix.exception.JobNotFoundException;
import com.divyansh.chronix.repository.JobExecutionRepository;
import com.divyansh.chronix.repository.JobRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobExecutionService {

    private final JobExecutionRepository jobExecutionRepository;
    private final JobRepository jobRepository;

    public JobExecutionService(
            JobExecutionRepository jobExecutionRepository,
            JobRepository jobRepository) {

        this.jobExecutionRepository = jobExecutionRepository;
        this.jobRepository = jobRepository;
    }
public List<JobExecutionResponse> getAllExecutions() {

    return jobExecutionRepository
            .findAllByOrderByStartedAtDesc()
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
}
    public List<JobExecutionResponse> getExecutionsByJobId(Long jobId) {

        if (!jobRepository.existsById(jobId)) {
            throw new JobNotFoundException(
                    "Job not found with id: " + jobId
            );
        }

        return jobExecutionRepository
                .findByJobIdOrderByStartedAtDesc(jobId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private JobExecutionResponse mapToResponse(JobExecution execution) {

        return new JobExecutionResponse(
                execution.getId(),
                execution.getJob().getId(),
                execution.getStatus(),
                execution.getStartedAt(),
                execution.getFinishedAt(),
                execution.getAttemptNumber(),
                execution.getErrorMessage()
        );
    }
}