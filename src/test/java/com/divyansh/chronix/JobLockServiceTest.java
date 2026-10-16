
package com.divyansh.chronix;

import com.divyansh.chronix.repository.JobLockRepository;
import com.divyansh.chronix.service.JobLockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobLockServiceTest {

    @Mock
    private JobLockRepository jobLockRepository;

    private JobLockService jobLockService;

    @BeforeEach
    void setUp() {
        jobLockService = new JobLockService(jobLockRepository);

        ReflectionTestUtils.setField(
                jobLockService,
                "lockDurationSeconds",
                60L
        );
    }

    @Test
    void workerCanAcquireUnlockedJob() {
        when(jobLockRepository.acquireLock(
                eq(23L),
                eq("worker-A"),
                any(),
                any()
        )).thenReturn(1);

        assertTrue(jobLockService.acquireLock(23L, "worker-A"));
    }

    @Test
    void workerCannotAcquireJobWhenAnotherWorkerOwnsLock() {
        when(jobLockRepository.acquireLock(
                eq(23L),
                eq("worker-B"),
                any(),
                any()
        )).thenReturn(0);

        assertFalse(jobLockService.acquireLock(23L, "worker-B"));
    }

    @Test
    void workerCannotReleaseAnotherWorkersLock() {
        when(jobLockRepository.releaseLock(23L, "worker-B"))
                .thenReturn(0);

        assertFalse(jobLockService.releaseLock(23L, "worker-B"));
    }

    @Test
    void ownerCanReleaseItsLock() {
        when(jobLockRepository.releaseLock(23L, "worker-A"))
                .thenReturn(1);

        assertTrue(jobLockService.releaseLock(23L, "worker-A"));
    }

    @Test
    void invalidJobIdIsRejected() {
        assertThrows(
                IllegalArgumentException.class,
                () -> jobLockService.acquireLock(0L, "worker-A")
        );

        verifyNoInteractions(jobLockRepository);
    }

    @Test
    void blankWorkerIdIsRejected() {
        assertThrows(
                IllegalArgumentException.class,
                () -> jobLockService.acquireLock(23L, " ")
        );

        verifyNoInteractions(jobLockRepository);
    }
}
