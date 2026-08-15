package com.divyansh.chronix.controller;

import com.divyansh.chronix.dto.CreateJobRequest;
import com.divyansh.chronix.dto.JobResponse;
import com.divyansh.chronix.service.JobService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    // Create Job
    @PostMapping
    public JobResponse createJob(
            @Valid @RequestBody CreateJobRequest request) {

        return jobService.createJob(request);
    }

    // Get All Jobs
    @GetMapping
    public List<JobResponse> getAllJobs() {

        return jobService.getAllJobs();
    }

    // Get Job By ID
    @GetMapping("/{id}")
    public JobResponse getJobById(
            @PathVariable Long id) {

        return jobService.getJobById(id);
    }

    // Update Job
    @PutMapping("/{id}")
    public JobResponse updateJob(
            @PathVariable Long id,
            @Valid @RequestBody CreateJobRequest request) {

        return jobService.updateJob(id, request);
    }

    // Cancel Job
    @DeleteMapping("/{id}/cancel")
    public JobResponse cancelJob(
            @PathVariable Long id) {

        return jobService.cancelJob(id);
    }

    // Delete Job
    @DeleteMapping("/{id}")
    public String deleteJob(
            @PathVariable Long id) {

        jobService.deleteJob(id);

        return "Job deleted successfully.";
    }
}