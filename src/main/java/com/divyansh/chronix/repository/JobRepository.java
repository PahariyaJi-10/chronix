package com.divyansh.chronix.repository;

import com.divyansh.chronix.entity.Job;
import com.divyansh.chronix.entity.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findByStatusAndScheduledAtLessThanEqual(
            JobStatus status,
            LocalDateTime scheduledAt
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT j
            FROM Job j
            WHERE j.status = :status
            AND j.scheduledAt <= :scheduledAt
            """)
    List<Job> findDueJobsForUpdate(
            @Param("status") JobStatus status,
            @Param("scheduledAt") LocalDateTime scheduledAt
    );

    @Modifying
    @Query("""
            UPDATE Job j
            SET j.status = :running,
                j.updatedAt = :updatedAt
            WHERE j.id = :id
            AND j.status = :pending
            """)
    int claimJob(
            @Param("id") Long id,
            @Param("pending") JobStatus pending,
            @Param("running") JobStatus running,
            @Param("updatedAt") LocalDateTime updatedAt
    );
}