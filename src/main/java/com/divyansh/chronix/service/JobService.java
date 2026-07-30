package com.divyansh.chronix.service;

import com.divyansh.chronix.dto.CreateJobRequest;
import com.divyansh.chronix.dto.JobResponse;
import com.divyansh.chronix.entity.Job;
import com.divyansh.chronix.entity.JobStatus;
import com.divyansh.chronix.repository.JobRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class JobService {

    private final JobRepository jobRepository;

    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public JobResponse createJob(CreateJobRequest request) {

        // Debug logs
        System.out.println("===== REQUEST RECEIVED =====");
        System.out.println("Name: " + request.getName());
        System.out.println("Type: " + request.getType());
        System.out.println("ScheduledAt: " + request.getScheduledAt());
        System.out.println("Payload: " + request.getPayload());
        System.out.println("============================");

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
}