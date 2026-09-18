package com.divyansh.chronix.controller;

import com.divyansh.chronix.dto.CreateJobRequest;
import com.divyansh.chronix.dto.JobResponse;
import com.divyansh.chronix.entity.JobPriority;
import com.divyansh.chronix.entity.JobStatus;
import com.divyansh.chronix.entity.JobType;
import com.divyansh.chronix.entity.ScheduleType;
import com.divyansh.chronix.service.JobService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

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

    // Get Jobs with Search, Filtering, Pagination and Sorting
    @GetMapping
    public Page<JobResponse> getAllJobs(

            // Search by job name
            @RequestParam(required = false) String search,

            // Filters
            @RequestParam(required = false) JobStatus status,
            @RequestParam(required = false) JobPriority priority,
            @RequestParam(required = false) JobType type,
            @RequestParam(required = false) ScheduleType scheduleType,

            // Pagination
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,

            // Sorting
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        if (page < 0) {
            throw new IllegalArgumentException(
                    "Page number cannot be negative"
            );
        }

        if (size < 1 || size > 100) {
            throw new IllegalArgumentException(
                    "Page size must be between 1 and 100"
            );
        }

        Sort.Direction sortDirection;

        try {

            sortDirection =
                    Sort.Direction.fromString(direction);

        } catch (IllegalArgumentException e) {

            throw new IllegalArgumentException(
                    "Sort direction must be 'asc' or 'desc'"
            );
        }

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by(sortDirection, sortBy)
                );

        return jobService.getAllJobs(
                search,
                status,
                priority,
                type,
                scheduleType,
                pageable
        );
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

    // Manual Retry Job
    @PostMapping("/{id}/retry")
    public JobResponse retryJob(
            @PathVariable Long id) {

        return jobService.retryJob(id);
    }

    // Cancel Job
    @DeleteMapping("/{id}/cancel")
    public JobResponse cancelJob(
            @PathVariable Long id) {

        return jobService.cancelJob(id);
    }

    // Pause Job
    @PostMapping("/{id}/pause")
    public JobResponse pauseJob(
            @PathVariable Long id) {

        return jobService.pauseJob(id);
    }

    // Resume Job
    @PostMapping("/{id}/resume")
    public JobResponse resumeJob(
            @PathVariable Long id) {

        return jobService.resumeJob(id);
    }

    // Delete Job
    @DeleteMapping("/{id}")
    public String deleteJob(
            @PathVariable Long id) {

        jobService.deleteJob(id);

        return "Job deleted successfully.";
    }
}