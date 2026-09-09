package com.divyansh.chronix.service;

import com.divyansh.chronix.dto.DeadLetterJobResponse;
import com.divyansh.chronix.entity.DeadLetterJob;
import com.divyansh.chronix.entity.Job;
import com.divyansh.chronix.entity.JobStatus;
import com.divyansh.chronix.exception.JobNotFoundException;
import com.divyansh.chronix.repository.DeadLetterJobRepository;
import com.divyansh.chronix.repository.JobRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeadLetterJobService {

    private final DeadLetterJobRepository deadLetterJobRepository;
    private final JobRepository jobRepository;

    public DeadLetterJobService(
            DeadLetterJobRepository deadLetterJobRepository,
            JobRepository jobRepository) {

        this.deadLetterJobRepository = deadLetterJobRepository;
        this.jobRepository = jobRepository;
    }

    public List<DeadLetterJobResponse> getAllDeadLetterJobs() {

        return deadLetterJobRepository
                .findAllByOrderByFailedAtDesc()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public DeadLetterJobResponse reprocessJob(Long deadLetterJobId) {

        DeadLetterJob deadLetterJob =
                deadLetterJobRepository.findById(deadLetterJobId)
                        .orElseThrow(() ->
                                new JobNotFoundException(
                                        "Dead-letter job not found with id: "
                                                + deadLetterJobId
                                ));

        Job job = deadLetterJob.getJob();

        // Create response before deleting the DLQ record
        DeadLetterJobResponse response =
                toResponse(deadLetterJob);

        // Reset job for a fresh retry cycle
        job.setStatus(JobStatus.PENDING);
        job.setRetryCount(0);
        job.setUpdatedAt(LocalDateTime.now());

        jobRepository.save(job);

        // Remove the job from the Dead-Letter Queue
        deadLetterJobRepository.delete(deadLetterJob);

        System.out.println(
                "Job reprocessed from Dead-Letter Queue: "
                        + job.getName()
        );

        return response;
    }

    private DeadLetterJobResponse toResponse(
            DeadLetterJob deadLetterJob) {

        return new DeadLetterJobResponse(
                deadLetterJob.getId(),
                deadLetterJob.getJob().getId(),
                deadLetterJob.getJobName(),
                deadLetterJob.getAttemptCount(),
                deadLetterJob.getErrorMessage(),
                deadLetterJob.getPayload(),
                deadLetterJob.getFailedAt()
        );
    }
}