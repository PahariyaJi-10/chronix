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

        return new JobResponse(
                savedJob.getId(),
                savedJob.getName(),
                savedJob.getType(),
                savedJob.getStatus()
        );
    }

    // Get All Jobs
    public List<JobResponse> getAllJobs() {

        return jobRepository.findAll()
                .stream()
                .map(job -> new JobResponse(
                        job.getId(),
                        job.getName(),
                        job.getType(),
                        job.getStatus()
                ))
                .collect(Collectors.toList());
    }

    // Get Job By ID
    public JobResponse getJobById(Long id) {

        Job job = jobRepository.findById(id)
                .orElseThrow(() ->
                        new JobNotFoundException("Job not found with id: " + id));

        return new JobResponse(
                job.getId(),
                job.getName(),
                job.getType(),
                job.getStatus()
        );
    }

    // Update Job
    public JobResponse updateJob(Long id, CreateJobRequest request) {

        Job job = jobRepository.findById(id)
                .orElseThrow(() ->
                        new JobNotFoundException("Job not found with id: " + id));

        job.setName(request.getName());
        job.setType(request.getType());
        job.setScheduledAt(request.getScheduledAt());
        job.setPayload(request.getPayload());
        job.setUpdatedAt(LocalDateTime.now());

        Job updatedJob = jobRepository.save(job);

        return new JobResponse(
                updatedJob.getId(),
                updatedJob.getName(),
                updatedJob.getType(),
                updatedJob.getStatus()
        );
    }

    // Delete Job
    public void deleteJob(Long id) {

        Job job = jobRepository.findById(id)
                .orElseThrow(() ->
                        new JobNotFoundException("Job not found with id: " + id));

        jobRepository.delete(job);
    }
}