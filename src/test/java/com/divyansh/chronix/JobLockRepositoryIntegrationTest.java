
package com.divyansh.chronix;

import com.divyansh.chronix.repository.JobLockRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JobLockRepositoryIntegrationTest {

    @Autowired
    private JobLockRepository jobLockRepository;

    @Test
    @Transactional
    void onlyOneWorkerCanAcquireAnActiveLock() {
        Long jobId = uniqueJobId();
        LocalDateTime now = LocalDateTime.now();

        try {
            int workerA = jobLockRepository.acquireLock(
                    jobId, "worker-A-" + UUID.randomUUID(),
                    now, now.plusMinutes(1));

            int workerB = jobLockRepository.acquireLock(
                    jobId, "worker-B-" + UUID.randomUUID(),
                    now, now.plusMinutes(1));

            assertEquals(1, workerA, "Worker A should acquire the lock");
            assertEquals(0, workerB, "Worker B should be denied the active lock");
        } finally {
            jobLockRepository.releaseLock(jobId,
                    "worker-A-" + "not-the-real-id");
            // Transaction rollback removes this test's database changes.
        }
    }

    @Test
    @Transactional
    void onlyTheOwnerCanReleaseALock() {
        Long jobId = uniqueJobId();
        String owner = "owner-" + UUID.randomUUID();
        String otherWorker = "other-" + UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        try {
            assertEquals(1, jobLockRepository.acquireLock(
                    jobId, owner, now, now.plusMinutes(1)));

            assertEquals(0, jobLockRepository.releaseLock(jobId, otherWorker));
            assertEquals(1, jobLockRepository.releaseLock(jobId, owner));
            assertEquals(1, jobLockRepository.acquireLock(
                    jobId, otherWorker, now, now.plusMinutes(1)));
        } finally {
            jobLockRepository.releaseLock(jobId, owner);
            jobLockRepository.releaseLock(jobId, otherWorker);
        }
    }

    @Test
    @Transactional
    void anotherWorkerCanAcquireAnExpiredLock() {
        Long jobId = uniqueJobId();
        String workerA = "worker-A-" + UUID.randomUUID();
        String workerB = "worker-B-" + UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        try {
            assertEquals(1, jobLockRepository.acquireLock(
                    jobId, workerA, now, now.minusSeconds(1)));

            assertEquals(1, jobLockRepository.acquireLock(
                    jobId, workerB, now, now.plusMinutes(1)));
        } finally {
            jobLockRepository.releaseLock(jobId, workerA);
            jobLockRepository.releaseLock(jobId, workerB);
        }
    }

    private Long uniqueJobId() {
        // Large, positive test-only IDs avoid modifying existing job rows.
        return Math.abs(UUID.randomUUID().getMostSignificantBits() % 1_000_000_000L)
                + 1_000_000_000L;
    }
}
