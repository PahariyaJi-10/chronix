package com.divyansh.chronix.repository;

import com.divyansh.chronix.entity.JobLock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

public interface JobLockRepository
        extends JpaRepository<JobLock, Long> {

    @Modifying
    @Transactional
    @Query(value = """
            INSERT INTO job_locks
                (job_id, locked_by, locked_at, expires_at)
            VALUES
                (:jobId, :lockedBy, :lockedAt, :expiresAt)
            ON CONFLICT (job_id)
            DO UPDATE SET
                locked_by = EXCLUDED.locked_by,
                locked_at = EXCLUDED.locked_at,
                expires_at = EXCLUDED.expires_at
            WHERE job_locks.expires_at <= CURRENT_TIMESTAMP
            """, nativeQuery = true)
    int acquireLock(
            @Param("jobId") Long jobId,
            @Param("lockedBy") String lockedBy,
            @Param("lockedAt") LocalDateTime lockedAt,
            @Param("expiresAt") LocalDateTime expiresAt
    );

    @Modifying
    @Transactional
    @Query(value = """
            DELETE FROM job_locks
            WHERE job_id = :jobId
              AND locked_by = :lockedBy
            """, nativeQuery = true)
    int releaseLock(
            @Param("jobId") Long jobId,
            @Param("lockedBy") String lockedBy
    );
}
