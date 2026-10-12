package com.divyansh.chronix.controller;

import com.divyansh.chronix.entity.Job;
import com.divyansh.chronix.entity.JobStatus;
import com.divyansh.chronix.repository.JobExecutionRepository;
import com.divyansh.chronix.repository.JobRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/jobs")
public class JobConcurrencyController {

    private final JobRepository jobRepository;
    private final JobExecutionRepository jobExecutionRepository;

    public JobConcurrencyController(
            JobRepository jobRepository,
            JobExecutionRepository jobExecutionRepository) {
        this.jobRepository = jobRepository;
        this.jobExecutionRepository = jobExecutionRepository;
    }

    @GetMapping("/{jobId}/concurrency")
    public Map<String, Object> getConcurrencyStatus(
            @PathVariable Long jobId) {

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Job not found with id: " + jobId
                ));

        long runningExecutions =
                jobExecutionRepository.countByJobIdAndStatus(
                        jobId,
                        JobStatus.RUNNING
                );

        Integer maxConcurrent =
                job.getMaxConcurrentExecutions();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("jobId", job.getId());
        response.put("jobName", job.getName());
        response.put("runningExecutions", runningExecutions);
        response.put("maxConcurrentExecutions", maxConcurrent);
        response.put(
                "concurrencyLimitConfigured",
                maxConcurrent != null
        );
        response.put(
                "limitReached",
                maxConcurrent != null
                        && runningExecutions >= maxConcurrent
        );
        response.put(
                "availableExecutionSlots",
                maxConcurrent == null
                        ? null
                        : Math.max(0L, maxConcurrent - runningExecutions)
        );

        return response;
    }
}
