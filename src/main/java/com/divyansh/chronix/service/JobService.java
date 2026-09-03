package com.divyansh.chronix.service;

import com.divyansh.chronix.dto.CreateJobRequest;
import com.divyansh.chronix.dto.JobResponse;
import com.divyansh.chronix.entity.Job;
import com.divyansh.chronix.entity.JobStatus;
import com.divyansh.chronix.exception.JobNotFoundException;
import com.divyansh.chronix.repository.JobRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobService {

    private final JobRepository jobRepository;

    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public JobResponse createJob(CreateJobRequest request) {

        Job job = new Job();

        job.setName(request.getName());
        job.setType(request.getType());
        job.setPriority(request.getPriority());
        job.setScheduledAt(request.getScheduledAt());
        job.setPayload(request.getPayload());

        // Set job dependency
        if (request.getDependsOnJobId() != null) {

            if (request.getDependsOnJobId() == null) {
                throw new RuntimeException(
                        "A job cannot depend on itself"
                );
            }

            Job dependencyJob = findJob(
                    request.getDependsOnJobId()
            );

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

        job.setName(request.getName());
        job.setType(request.getType());
        job.setPriority(request.getPriority());
        job.setScheduledAt(request.getScheduledAt());
        job.setPayload(request.getPayload());

        // Update job dependency
        if (request.getDependsOnJobId() != null) {

            if (request.getDependsOnJobId().equals(id)) {
                throw new RuntimeException(
                        "A job cannot depend on itself"
                );
            }

            Job dependencyJob = findJob(
                    request.getDependsOnJobId()
            );

            job.setDependsOn(dependencyJob);

        } else {

            job.setDependsOn(null);
        }

        job.setUpdatedAt(LocalDateTime.now());

        Job updatedJob = jobRepository.save(job);

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

        return toResponse(jobRepository.save(job));
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

        return toResponse(jobRepository.save(job));
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

        return toResponse(jobRepository.save(job));
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

        return toResponse(jobRepository.save(job));
    }

    public void deleteJob(Long id) {

        Job job = findJob(id);

        jobRepository.delete(job);
    }

    private Job findJob(Long id) {

        return jobRepository.findById(id)
                .orElseThrow(() ->
                        new JobNotFoundException(
                                "Job not found with id: " + id
                        ));
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
                : null
);
    }
}