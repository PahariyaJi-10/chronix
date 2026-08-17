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

    // Create Job
    public JobResponse createJob(CreateJobRequest request) {

        Job job = new Job();

        job.setName(request.getName());
        job.setType(request.getType());
        job.setScheduledAt(request.getScheduledAt());
        job.setPayload(request.getPayload());

        job.setStatus(JobStatus.PENDING);
        job.setRetryCount(0);

        job.setCreatedAt(LocalDateTime.now());
        job.setUpdatedAt(LocalDateTime.now());

        Job savedJob = jobRepository.save(job);

        return toResponse(savedJob);
    }

    // Get All Jobs
    public List<JobResponse> getAllJobs() {

        return jobRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Get Job By ID
    public JobResponse getJobById(Long id) {

        Job job = findJob(id);

        return toResponse(job);
    }

    // Update Job
    public JobResponse updateJob(
            Long id,
            CreateJobRequest request) {

        Job job = findJob(id);

        job.setName(request.getName());
        job.setType(request.getType());
        job.setScheduledAt(request.getScheduledAt());
        job.setPayload(request.getPayload());
        job.setUpdatedAt(LocalDateTime.now());

        Job updatedJob = jobRepository.save(job);

        return toResponse(updatedJob);
    }

    // Cancel Job
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

    // Pause Job
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

    // Resume Job
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

    // Delete Job
    public void deleteJob(Long id) {

        Job job = findJob(id);

        jobRepository.delete(job);
    }

    // Find Job
    private Job findJob(Long id) {

        return jobRepository.findById(id)
                .orElseThrow(() ->
                        new JobNotFoundException(
                                "Job not found with id: " + id
                        ));
    }

    // Convert Entity to Response
    private JobResponse toResponse(Job job) {

        return new JobResponse(
                job.getId(),
                job.getName(),
                job.getType(),
                job.getStatus()
        );
    }
}