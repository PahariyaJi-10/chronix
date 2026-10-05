package com.divyansh.chronix.service;

import com.divyansh.chronix.dto.CreateJobRequest;
import com.divyansh.chronix.dto.JobResponse;
import com.divyansh.chronix.entity.Job;
import com.divyansh.chronix.entity.JobAuditAction;
import com.divyansh.chronix.entity.JobPriority;
import com.divyansh.chronix.entity.JobStatus;
import com.divyansh.chronix.entity.JobType;
import com.divyansh.chronix.entity.ScheduleType;
import com.divyansh.chronix.exception.JobNotFoundException;
import com.divyansh.chronix.repository.JobRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final JobAuditLogService jobAuditLogService;

    public JobService(
            JobRepository jobRepository,
            JobAuditLogService jobAuditLogService) {

        this.jobRepository = jobRepository;
        this.jobAuditLogService = jobAuditLogService;
    }

    // Create a new job
    public JobResponse createJob(CreateJobRequest request) {

        validateSchedule(
                request.getScheduleType(),
                request.getCronExpression()
        );

        Job job = new Job();

        job.setName(request.getName());
        job.setType(request.getType());
        job.setPriority(request.getPriority());
        job.setScheduledAt(request.getScheduledAt());
        job.setPayload(request.getPayload());

        job.setScheduleType(request.getScheduleType());
        job.setCronExpression(request.getCronExpression());

        // Set job tags
        job.setTags(request.getTags());

        // Set rate limiting
        job.setRateLimit(request.getRateLimit());
        job.setRateLimitWindowSeconds(
                request.getRateLimitWindowSeconds()
        );

        // Set dependency
        if (request.getDependsOnJobId() != null) {

            Job dependencyJob =
                    findJob(request.getDependsOnJobId());

            if (createsCircularDependency(
                    job,
                    dependencyJob)) {

                throw new RuntimeException(
                        "Circular job dependency detected"
                );
            }

            job.setDependsOn(dependencyJob);
        }

        job.setStatus(JobStatus.PENDING);
        job.setRetryCount(0);

        job.setCreatedAt(LocalDateTime.now());
        job.setUpdatedAt(LocalDateTime.now());

        Job savedJob = jobRepository.save(job);

        jobAuditLogService.log(
                savedJob,
                JobAuditAction.JOB_CREATED,
                "Job created successfully"
        );

        return toResponse(savedJob);
    }

    // Get all jobs with dynamic filtering
    public Page<JobResponse> getAllJobs(
            String search,
            String tag,
            JobStatus status,
            JobPriority priority,
            JobType type,
            ScheduleType scheduleType,
            Pageable pageable) {

        String searchTerm =
                search != null && !search.isBlank()
                        ? search.trim()
                        : null;

        String tagTerm =
                tag != null && !tag.isBlank()
                        ? tag.trim()
                        : null;

        Page<Job> jobs = jobRepository.findJobsWithFilters(
                searchTerm,
                tagTerm,
                status,
                priority,
                type,
                scheduleType,
                pageable
        );

        return jobs.map(this::toResponse);
    }

    // Get job by ID
    public JobResponse getJobById(Long id) {

        Job job = findJob(id);

        return toResponse(job);
    }

    // Update job
    public JobResponse updateJob(
            Long id,
            CreateJobRequest request) {

        Job job = findJob(id);

        validateSchedule(
                request.getScheduleType(),
                request.getCronExpression()
        );

        job.setName(request.getName());
        job.setType(request.getType());
        job.setPriority(request.getPriority());
        job.setScheduledAt(request.getScheduledAt());
        job.setPayload(request.getPayload());

        job.setScheduleType(request.getScheduleType());
        job.setCronExpression(request.getCronExpression());

        // Update job tags
        job.setTags(request.getTags());

        // Update rate limiting
        job.setRateLimit(request.getRateLimit());
        job.setRateLimitWindowSeconds(
                request.getRateLimitWindowSeconds()
        );

        // Update dependency
        if (request.getDependsOnJobId() != null) {

            if (request.getDependsOnJobId().equals(id)) {

                throw new RuntimeException(
                        "A job cannot depend on itself"
                );
            }

            Job dependencyJob =
                    findJob(request.getDependsOnJobId());

            if (createsCircularDependency(
                    job,
                    dependencyJob)) {

                throw new RuntimeException(
                        "Circular job dependency detected"
                );
            }

            job.setDependsOn(dependencyJob);

        } else {

            job.setDependsOn(null);
        }

        job.setUpdatedAt(LocalDateTime.now());

        Job updatedJob =
                jobRepository.save(job);

        jobAuditLogService.log(
                updatedJob,
                JobAuditAction.JOB_UPDATED,
                "Job updated successfully"
        );

        return toResponse(updatedJob);
    }

    // Cancel job
    public JobResponse cancelJob(Long id) {

        Job job = findJob(id);

        if (job.getStatus() != JobStatus.PENDING) {

            throw new RuntimeException(
                    "Only PENDING jobs can be cancelled"
            );
        }

        job.setStatus(JobStatus.CANCELLED);
        job.setUpdatedAt(LocalDateTime.now());

        Job cancelledJob =
                jobRepository.save(job);

        jobAuditLogService.log(
                cancelledJob,
                JobAuditAction.JOB_CANCELLED,
                "Job cancelled successfully"
        );

        return toResponse(cancelledJob);
    }

    // Pause job
    public JobResponse pauseJob(Long id) {

        Job job = findJob(id);

        if (job.getStatus() != JobStatus.PENDING) {

            throw new RuntimeException(
                    "Only PENDING jobs can be paused"
            );
        }

        job.setStatus(JobStatus.PAUSED);
        job.setUpdatedAt(LocalDateTime.now());

        Job pausedJob =
                jobRepository.save(job);

        jobAuditLogService.log(
                pausedJob,
                JobAuditAction.JOB_PAUSED,
                "Job paused successfully"
        );

        return toResponse(pausedJob);
    }

    // Resume job
    public JobResponse resumeJob(Long id) {

        Job job = findJob(id);

        if (job.getStatus() != JobStatus.PAUSED) {

            throw new RuntimeException(
                    "Only PAUSED jobs can be resumed"
            );
        }

        job.setStatus(JobStatus.PENDING);
        job.setUpdatedAt(LocalDateTime.now());

        Job resumedJob =
                jobRepository.save(job);

        jobAuditLogService.log(
                resumedJob,
                JobAuditAction.JOB_RESUMED,
                "Job resumed successfully"
        );

        return toResponse(resumedJob);
    }

    // Retry failed job
    public JobResponse retryJob(Long id) {

        Job job = findJob(id);

        if (job.getStatus() != JobStatus.FAILED) {

            throw new RuntimeException(
                    "Only FAILED jobs can be retried"
            );
        }

        job.setStatus(JobStatus.PENDING);
        job.setRetryCount(0);
        job.setUpdatedAt(LocalDateTime.now());

        Job retriedJob =
                jobRepository.save(job);

        jobAuditLogService.log(
                retriedJob,
                JobAuditAction.JOB_RETRY_REQUESTED,
                "Manual retry requested"
        );

        return toResponse(retriedJob);
    }

    // Delete job
    public void deleteJob(Long id) {

        Job job = findJob(id);

        jobRepository.delete(job);
    }

    // Validate scheduling configuration
    private void validateSchedule(
            ScheduleType scheduleType,
            String cronExpression) {

        if (scheduleType == null) {

            throw new RuntimeException(
                    "Schedule type is required"
            );
        }

        if (scheduleType == ScheduleType.CRON) {

            if (cronExpression == null
                    || cronExpression.isBlank()) {

                throw new RuntimeException(
                        "Cron expression is required for CRON schedule"
                );
            }

            try {

                org.springframework.scheduling.support.CronExpression
                        .parse(cronExpression);

            } catch (IllegalArgumentException e) {

                throw new RuntimeException(
                        "Invalid cron expression: "
                                + cronExpression
                );
            }
        }

        if (scheduleType != ScheduleType.CRON
                && cronExpression != null
                && !cronExpression.isBlank()) {

            throw new RuntimeException(
                    "Cron expression is only allowed for CRON schedule"
            );
        }
    }

    // Check for circular job dependencies
    private boolean createsCircularDependency(
            Job currentJob,
            Job dependencyJob) {

        Set<Long> visitedJobs = new HashSet<>();

        Job current = dependencyJob;

        while (current != null) {

            Long currentId = current.getId();

            if (currentId != null) {

                if (currentId.equals(currentJob.getId())) {

                    return true;
                }

                if (!visitedJobs.add(currentId)) {

                    return true;
                }
            }

            current = current.getDependsOn();
        }

        return false;
    }

    // Find job or throw exception
    private Job findJob(Long id) {

        return jobRepository.findById(id)
                .orElseThrow(() ->
                        new JobNotFoundException(
                                "Job not found with id: " + id
                        )
                );
    }

    // Convert Job entity to JobResponse
    private JobResponse toResponse(Job job) {

        return new JobResponse(
                job.getId(),
                job.getName(),
                job.getType(),
                job.getStatus(),
                job.getPriority(),
                job.getDependsOn() != null
                        ? job.getDependsOn().getId()
                        : null,
                job.getScheduleType(),
                job.getCronExpression(),
                job.getTags(),
                job.getRateLimit(),
                job.getRateLimitWindowSeconds()
        );
    }
}