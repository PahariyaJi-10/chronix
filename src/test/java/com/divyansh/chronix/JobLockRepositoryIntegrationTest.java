
package com.divyansh.chronix;

import com.divyansh.chronix.repository.JobLockRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class JobLockRepositoryIntegrationTest {

    @Autowired
    private JobLockRepository jobLockRepository;

    @Test
    void onlyOneWorkerCanAcquireAnActiveLock() {
        Long jobId = uniqueJobId();
        String workerA = "worker-A-" + UUID.randomUUID();
        String workerB = "worker-B-" + UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        try {
            assertEquals(1, jobLockRepository.acquireLock(
                    jobId, workerA, now, now.plusMinutes(1)));

            assertEquals(0, jobLockRepository.acquireLock(
                    jobId, workerB, now, now.plusMinutes(1)));
        } finally {
            jobLockRepository.releaseLock(jobId, workerA);
            jobLockRepository.releaseLock(jobId, workerB);
        }
    }

    @Test
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

    @Test
    void simultaneousWorkersCannotBothAcquireTheSameLock() throws Exception {
        Long jobId = uniqueJobId();
        String workerA = "worker-A-" + UUID.randomUUID();
        String workerB = "worker-B-" + UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(2);

        try {
            Future<Integer> resultA = executor.submit(() -> {
                ready.countDown();
                if (!start.await(10, TimeUnit.SECONDS)) {
                    throw new IllegalStateException("Start signal timed out");
                }
                return jobLockRepository.acquireLock(
                        jobId, workerA, now, now.plusMinutes(1));
            });

            Future<Integer> resultB = executor.submit(() -> {
                ready.countDown();
                if (!start.await(10, TimeUnit.SECONDS)) {
                    throw new IllegalStateException("Start signal timed out");
                }
                return jobLockRepository.acquireLock(
                        jobId, workerB, now, now.plusMinutes(1));
            });

            assertTrue(ready.await(10, TimeUnit.SECONDS),
                    "Both workers should be ready");

            start.countDown();

            int acquiredA = resultA.get(15, TimeUnit.SECONDS);
            int acquiredB = resultB.get(15, TimeUnit.SECONDS);

            assertEquals(1, acquiredA + acquiredB,
                    "Exactly one worker should acquire the lock");
        } finally {
            start.countDown();
            executor.shutdownNow();
            jobLockRepository.releaseLock(jobId, workerA);
            jobLockRepository.releaseLock(jobId, workerB);
        }
    }

    private Long uniqueJobId() {
        return Math.abs(
                UUID.randomUUID().getMostSignificantBits() % 1_000_000_000L
        ) + 1_000_000_000L;
    }
}
