package com.divyansh.chronix.service;

import com.divyansh.chronix.dto.CreateJobRequest;
import com.divyansh.chronix.dto.JobResponse;
import com.divyansh.chronix.entity.Job;
import com.divyansh.chronix.entity.JobStatus;
import com.divyansh.chronix.entity.ScheduleType;
import com.divyansh.chronix.exception.JobNotFoundException;
import com.divyansh.chronix.repository.JobRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class JobService {

    private final JobRepository jobRepository;

    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

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

        return toResponse(savedJob);
    }

    public List<JobResponse> getAllJobs() {

        return jobRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public JobResponse getJobById(Long id) {

        Job job = findJob(id);

        return toResponse(job);
    }

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

        return toResponse(updatedJob);
    }

    public JobResponse cancelJob(Long id) {

        Job job = findJob(id);

        if (job.getStatus() != JobStatus.PENDING) {

            throw new RuntimeException(
                    "Only PENDING jobs can be cancelled"
            );
        }

        job.setStatus(JobStatus.CANCELLED);
        job.setUpdatedAt(LocalDateTime.now());

        return toResponse(
                jobRepository.save(job)
        );
    }

    public JobResponse pauseJob(Long id) {

        Job job = findJob(id);

        if (job.getStatus() != JobStatus.PENDING) {

            throw new RuntimeException(
                    "Only PENDING jobs can be paused"
            );
        }

        job.setStatus(JobStatus.PAUSED);
        job.setUpdatedAt(LocalDateTime.now());

        return toResponse(
                jobRepository.save(job)
        );
    }

    public JobResponse resumeJob(Long id) {

        Job job = findJob(id);

        if (job.getStatus() != JobStatus.PAUSED) {

            throw new RuntimeException(
                    "Only PAUSED jobs can be resumed"
            );
        }

        job.setStatus(JobStatus.PENDING);
        job.setUpdatedAt(LocalDateTime.now());

        return toResponse(
                jobRepository.save(job)
        );
    }

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

        return toResponse(
                jobRepository.save(job)
        );
    }

    public void deleteJob(Long id) {

        Job job = findJob(id);

        jobRepository.delete(job);
    }

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

    /**
     * Checks whether assigning dependencyJob as the dependency
     * of currentJob would create a circular dependency.
     *
     * Example:
     *
     * Job A -> Job B
     * Job B -> Job C
     * Job C -> Job A
     *
     * This method detects the cycle before saving.
     */
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

    private Job findJob(Long id) {

        return jobRepository.findById(id)
                .orElseThrow(() ->
                        new JobNotFoundException(
                                "Job not found with id: " + id
                        )
                );
    }

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
                job.getCronExpression()
        );
    }
}