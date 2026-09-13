package com.divyansh.chronix.repository;

import com.divyansh.chronix.entity.Job;
import com.divyansh.chronix.entity.JobPriority;
import com.divyansh.chronix.entity.JobStatus;
import com.divyansh.chronix.entity.JobType;
import com.divyansh.chronix.entity.ScheduleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;

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
            ORDER BY
                CASE j.priority
                    WHEN com.divyansh.chronix.entity.JobPriority.HIGH THEN 1
                    WHEN com.divyansh.chronix.entity.JobPriority.MEDIUM THEN 2
                    WHEN com.divyansh.chronix.entity.JobPriority.LOW THEN 3
                    ELSE 4
                END,
                j.scheduledAt ASC
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

    long countByStatus(JobStatus status);

    List<Job> findByStatus(JobStatus status);

    List<Job> findByPriority(JobPriority priority);

    List<Job> findByType(JobType type);

    List<Job> findByScheduleType(ScheduleType scheduleType);

    List<Job> findByStatusAndPriority(
            JobStatus status,
            JobPriority priority
    );

    List<Job> findByStatusAndType(
            JobStatus status,
            JobType type
    );

    List<Job> findByStatusAndScheduleType(
            JobStatus status,
            ScheduleType scheduleType
    );

    List<Job> findByPriorityAndType(
            JobPriority priority,
            JobType type
    );
}