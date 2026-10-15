package com.divyansh.chronix.service;

import com.divyansh.chronix.repository.JobLockRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class JobLockService {

    private final JobLockRepository jobLockRepository;

    @Value("${chronix.job-lock.duration-seconds:60}")
    private long lockDurationSeconds;

    public JobLockService(JobLockRepository jobLockRepository) {
        this.jobLockRepository = jobLockRepository;
    }

    /**
     * Attempts to acquire a distributed lock for a job.
     *
     * @return true if acquired, false if another worker
     *         holds an unexpired lock.
     */
    public boolean acquireLock(Long jobId, String workerId) {

        validateInputs(jobId, workerId);

        if (lockDurationSeconds <= 0) {
            throw new IllegalStateException(
                    "Job lock duration must be positive"
            );
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt =
                now.plusSeconds(lockDurationSeconds);

        int rowsAffected = jobLockRepository.acquireLock(
                jobId,
                workerId,
                now,
                expiresAt
        );

        return rowsAffected > 0;
    }

    /**
     * Releases a lock only if it belongs to this worker.
     *
     * @return true if the lock was released, false otherwise.
     */
    public boolean releaseLock(Long jobId, String workerId) {

        validateInputs(jobId, workerId);

        int rowsAffected = jobLockRepository.releaseLock(
                jobId,
                workerId
        );

        return rowsAffected > 0;
    }

    private void validateInputs(Long jobId, String workerId) {

        if (jobId == null || jobId <= 0) {
            throw new IllegalArgumentException(
                    "Job ID must be a positive number"
            );
        }

        if (workerId == null || workerId.isBlank()) {
            throw new IllegalArgumentException(
                    "Worker ID must not be blank"
            );
        }
    }
}
