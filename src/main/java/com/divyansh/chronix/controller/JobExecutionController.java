package com.divyansh.chronix.controller;

import com.divyansh.chronix.dto.JobExecutionResponse;
import com.divyansh.chronix.service.JobExecutionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobExecutionController {

    private final JobExecutionService jobExecutionService;

    public JobExecutionController(
            JobExecutionService jobExecutionService) {

        this.jobExecutionService = jobExecutionService;
    }
@GetMapping("/executions")
public List<JobExecutionResponse> getAllExecutions() {

    return jobExecutionService.getAllExecutions();
}
    @GetMapping("/{jobId}/executions")
    public List<JobExecutionResponse> getJobExecutions(
            @PathVariable Long jobId) {

        return jobExecutionService.getExecutionsByJobId(jobId);
    }
}