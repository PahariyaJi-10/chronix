package com.divyansh.chronix.controller;

import com.divyansh.chronix.dto.JobExecutionResponse;
import com.divyansh.chronix.service.JobExecutionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class JobExecutionController {

    private final JobExecutionService jobExecutionService;

    public JobExecutionController(
            JobExecutionService jobExecutionService) {

        this.jobExecutionService = jobExecutionService;
    }

    // Get all execution history
    @GetMapping("/executions")
    public List<JobExecutionResponse> getAllExecutions() {

        return jobExecutionService.getAllExecutions();
    }

    // Get execution history for a specific job
    @GetMapping("/jobs/{jobId}/executions")
    public List<JobExecutionResponse> getJobExecutions(
            @PathVariable Long jobId) {

        return jobExecutionService.getExecutionsByJobId(jobId);
    }
}