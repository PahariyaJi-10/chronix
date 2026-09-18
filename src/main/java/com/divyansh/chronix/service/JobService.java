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

        jobAuditLogService.log(
                savedJob,
                JobAuditAction.JOB_CREATED,
                "Job created successfully"
        );

        return toResponse(savedJob);
    }

    public Page<JobResponse> getAllJobs(
            String search,
            JobStatus status,
            JobPriority priority,
            JobType type,
            ScheduleType scheduleType,
            Pageable pageable) {

        Page<Job> jobs;

        /*
         * Search + filters
         */
        if (search != null && !search.isBlank()) {

            String searchTerm = search.trim();

            if (status != null) {

                jobs = jobRepository
                        .findByNameContainingIgnoreCaseAndStatus(
                                searchTerm,
                                status,
                                pageable
                        );

            } else if (priority != null) {

                jobs = jobRepository
                        .findByNameContainingIgnoreCaseAndPriority(
                                searchTerm,
                                priority,
                                pageable
                        );

            } else if (type != null) {

                jobs = jobRepository
                        .findByNameContainingIgnoreCaseAndType(
                                searchTerm,
                                type,
                                pageable
                        );

            } else if (scheduleType != null) {

                jobs = jobRepository
                        .findByNameContainingIgnoreCaseAndScheduleType(
                                searchTerm,
                                scheduleType,
                                pageable
                        );

            } else {

                jobs = jobRepository
                        .findByNameContainingIgnoreCase(
                                searchTerm,
                                pageable
                        );
            }

        } else {

            /*
             * Existing filters
             */
            if (status != null && priority != null) {

                jobs = jobRepository.findByStatusAndPriority(
                        status,
                        priority,
                        pageable
                );

            } else if (status != null && type != null) {

                jobs = jobRepository.findByStatusAndType(
                        status,
                        type,
                        pageable
                );

            } else if (status != null && scheduleType != null) {

                jobs = jobRepository.findByStatusAndScheduleType(
                        status,
                        scheduleType,
                        pageable
                );

            } else if (priority != null && type != null) {

                jobs = jobRepository.findByPriorityAndType(
                        priority,
                        type,
                        pageable
                );

            } else if (status != null) {

                jobs = jobRepository.findByStatus(
                        status,
                        pageable
                );

            } else if (priority != null) {

                jobs = jobRepository.findByPriority(
                        priority,
                        pageable
                );

            } else if (type != null) {

                jobs = jobRepository.findByType(
                        type,
                        pageable
                );

            } else if (scheduleType != null) {

                jobs = jobRepository.findByScheduleType(
                        scheduleType,
                        pageable
                );

            } else {

                jobs = jobRepository.findAll(pageable);
            }
        }

        return jobs.map(this::toResponse);
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

        jobAuditLogService.log(
                updatedJob,
                JobAuditAction.JOB_UPDATED,
                "Job updated successfully"
        );

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

        Job cancelledJob =
                jobRepository.save(job);

        jobAuditLogService.log(
                cancelledJob,
                JobAuditAction.JOB_CANCELLED,
                "Job cancelled successfully"
        );

        return toResponse(cancelledJob);
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

        Job pausedJob =
                jobRepository.save(job);

        jobAuditLogService.log(
                pausedJob,
                JobAuditAction.JOB_PAUSED,
                "Job paused successfully"
        );

        return toResponse(pausedJob);
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

        Job resumedJob =
                jobRepository.save(job);

        jobAuditLogService.log(
                resumedJob,
                JobAuditAction.JOB_RESUMED,
                "Job resumed successfully"
        );

        return toResponse(resumedJob);
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

        Job retriedJob =
                jobRepository.save(job);

        jobAuditLogService.log(
                retriedJob,
                JobAuditAction.JOB_RETRY_REQUESTED,
                "Manual retry requested"
        );

        return toResponse(retriedJob);
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