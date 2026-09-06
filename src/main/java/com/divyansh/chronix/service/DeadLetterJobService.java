package com.divyansh.chronix.service;

import com.divyansh.chronix.dto.DeadLetterJobResponse;
import com.divyansh.chronix.entity.DeadLetterJob;
import com.divyansh.chronix.repository.DeadLetterJobRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeadLetterJobService {

    private final DeadLetterJobRepository deadLetterJobRepository;

    public DeadLetterJobService(
            DeadLetterJobRepository deadLetterJobRepository) {
        this.deadLetterJobRepository = deadLetterJobRepository;
    }

    public List<DeadLetterJobResponse> getAllDeadLetterJobs() {

        return deadLetterJobRepository
                .findAllByOrderByFailedAtDesc()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
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