package com.divyansh.chronix.service;

import com.divyansh.chronix.dto.ExecutionMetricsResponse;
import com.divyansh.chronix.dto.JobExecutionResponse;
import com.divyansh.chronix.entity.JobExecution;
import com.divyansh.chronix.entity.JobStatus;
import com.divyansh.chronix.exception.JobNotFoundException;
import com.divyansh.chronix.repository.JobExecutionRepository;
import com.divyansh.chronix.repository.JobRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
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

    // Get all executions
    public List<JobExecutionResponse> getAllExecutions() {

        return jobExecutionRepository
                .findAllByOrderByStartedAtDesc()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Get executions for a specific job
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

    // Get global execution statistics
    public ExecutionMetricsResponse getExecutionMetrics() {

        long totalExecutions =
                jobExecutionRepository.count();

        long successfulExecutions =
                jobExecutionRepository.countByStatus(
                        JobStatus.COMPLETED
                );

        long failedExecutions =
                jobExecutionRepository.countByStatus(
                        JobStatus.FAILED
                );

        long runningExecutions =
                jobExecutionRepository.countByStatus(
                        JobStatus.RUNNING
                );

        double successRate = 0.0;
        double failureRate = 0.0;

        if (totalExecutions > 0) {

            successRate =
                    (successfulExecutions * 100.0)
                            / totalExecutions;

            failureRate =
                    (failedExecutions * 100.0)
                            / totalExecutions;
        }

        long totalExecutionTimeMs = 0;
        long completedExecutionCount = 0;

        List<JobExecution> executions =
                jobExecutionRepository.findAll();

        for (JobExecution execution : executions) {

            if (execution.getStartedAt() != null
                    && execution.getFinishedAt() != null) {

                long duration =
                        Duration.between(
                                execution.getStartedAt(),
                                execution.getFinishedAt()
                        ).toMillis();

                totalExecutionTimeMs += duration;
                completedExecutionCount++;
            }
        }

        double averageExecutionTimeMs = 0.0;

        if (completedExecutionCount > 0) {

            averageExecutionTimeMs =
                    (double) totalExecutionTimeMs
                            / completedExecutionCount;
        }

        return new ExecutionMetricsResponse(
                totalExecutions,
                successfulExecutions,
                failedExecutions,
                runningExecutions,
                successRate,
                failureRate,
                totalExecutionTimeMs,
                averageExecutionTimeMs
        );
    }

    // Get execution statistics for a specific job
    public ExecutionMetricsResponse getExecutionMetricsByJobId(
            Long jobId) {

        if (!jobRepository.existsById(jobId)) {

            throw new JobNotFoundException(
                    "Job not found with id: " + jobId
            );
        }

        List<JobExecution> executions =
                jobExecutionRepository
                        .findByJobIdOrderByStartedAtDesc(jobId);

        long totalExecutions = executions.size();

        long successfulExecutions =
                executions.stream()
                        .filter(execution ->
                                execution.getStatus()
                                        == JobStatus.COMPLETED)
                        .count();

        long failedExecutions =
                executions.stream()
                        .filter(execution ->
                                execution.getStatus()
                                        == JobStatus.FAILED)
                        .count();

        long runningExecutions =
                executions.stream()
                        .filter(execution ->
                                execution.getStatus()
                                        == JobStatus.RUNNING)
                        .count();

        double successRate = 0.0;
        double failureRate = 0.0;

        if (totalExecutions > 0) {

            successRate =
                    (successfulExecutions * 100.0)
                            / totalExecutions;

            failureRate =
                    (failedExecutions * 100.0)
                            / totalExecutions;
        }

        long totalExecutionTimeMs = 0;
        long completedExecutionCount = 0;

        for (JobExecution execution : executions) {

            if (execution.getStartedAt() != null
                    && execution.getFinishedAt() != null) {

                long duration =
                        Duration.between(
                                execution.getStartedAt(),
                                execution.getFinishedAt()
                        ).toMillis();

                totalExecutionTimeMs += duration;
                completedExecutionCount++;
            }
        }

        double averageExecutionTimeMs = 0.0;

        if (completedExecutionCount > 0) {

            averageExecutionTimeMs =
                    (double) totalExecutionTimeMs
                            / completedExecutionCount;
        }

        return new ExecutionMetricsResponse(
                jobId,
                totalExecutions,
                successfulExecutions,
                failedExecutions,
                runningExecutions,
                successRate,
                failureRate,
                totalExecutionTimeMs,
                averageExecutionTimeMs
        );
    }

    // Convert entity to response DTO
    private JobExecutionResponse mapToResponse(
            JobExecution execution) {

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