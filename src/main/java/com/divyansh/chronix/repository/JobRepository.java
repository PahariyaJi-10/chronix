package com.divyansh.chronix.repository;

import com.divyansh.chronix.entity.Job;
import com.divyansh.chronix.entity.JobPriority;
import com.divyansh.chronix.entity.JobStatus;
import com.divyansh.chronix.entity.JobType;
import com.divyansh.chronix.entity.ScheduleType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {

    // Find jobs by status and scheduled time
    List<Job> findByStatusAndScheduledAtLessThanEqual(
            JobStatus status,
            LocalDateTime scheduledAt
    );

    // Find due jobs with priority-based ordering and row locking
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

    // Claim a job for execution
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

    // Count jobs by status
    long countByStatus(JobStatus status);

    // =========================================================
    // Basic Filters
    // =========================================================

    List<Job> findByStatus(JobStatus status);

    List<Job> findByPriority(JobPriority priority);

    List<Job> findByType(JobType type);

    List<Job> findByScheduleType(ScheduleType scheduleType);

    // =========================================================
    // Combined Filters
    // =========================================================

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

    // =========================================================
    // Pagination
    // =========================================================

    Page<Job> findAll(Pageable pageable);

    Page<Job> findByStatus(
            JobStatus status,
            Pageable pageable
    );

    Page<Job> findByPriority(
            JobPriority priority,
            Pageable pageable
    );

    Page<Job> findByType(
            JobType type,
            Pageable pageable
    );

    Page<Job> findByScheduleType(
            ScheduleType scheduleType,
            Pageable pageable
    );

    // =========================================================
    // Pagination + Combined Filters
    // =========================================================

    Page<Job> findByStatusAndPriority(
            JobStatus status,
            JobPriority priority,
            Pageable pageable
    );

    Page<Job> findByStatusAndType(
            JobStatus status,
            JobType type,
            Pageable pageable
    );

    Page<Job> findByStatusAndScheduleType(
            JobStatus status,
            ScheduleType scheduleType,
            Pageable pageable
    );

    Page<Job> findByPriorityAndType(
            JobPriority priority,
            JobType type,
            Pageable pageable
    );

    // =========================================================
    // Search by Job Name
    // =========================================================

    Page<Job> findByNameContainingIgnoreCase(
            String name,
            Pageable pageable
    );

    // =========================================================
    // Search + Status
    // =========================================================

    Page<Job> findByNameContainingIgnoreCaseAndStatus(
            String name,
            JobStatus status,
            Pageable pageable
    );

    // =========================================================
    // Search + Priority
    // =========================================================

    Page<Job> findByNameContainingIgnoreCaseAndPriority(
            String name,
            JobPriority priority,
            Pageable pageable
    );

    // =========================================================
    // Search + Type
    // =========================================================

    Page<Job> findByNameContainingIgnoreCaseAndType(
            String name,
            JobType type,
            Pageable pageable
    );

    // =========================================================
    // Search + Schedule Type
    // =========================================================

    Page<Job> findByNameContainingIgnoreCaseAndScheduleType(
            String name,
            ScheduleType scheduleType,
            Pageable pageable
    );

    // =========================================================
    // Search by Job Tag
    // =========================================================

    Page<Job> findByTagsContainingIgnoreCase(
            String tag,
            Pageable pageable
    );

    // =========================================================
    // Dynamic Combined Filtering
    //
    // Search + Tag + Status + Priority + Type + Schedule Type
    //
    // Search and tag are converted to empty strings when null.
    // This avoids PostgreSQL's "lower(bytea)" error caused by
    // untyped NULL parameters.
    // =========================================================

    @Query("""
            SELECT j
            FROM Job j
            WHERE
                LOWER(j.name) LIKE
                LOWER(
                    CONCAT(
                        '%',
                        COALESCE(:search, ''),
                        '%'
                    )
                )

                AND

                LOWER(
                    CONCAT(
                        ',',
                        COALESCE(j.tags, ''),
                        ','
                    )
                ) LIKE
                CONCAT(
                    '%,',
                    LOWER(
                        COALESCE(:tag, '')
                    ),
                    ',%'
                )

                AND

                (
                    :status IS NULL
                    OR j.status = :status
                )

                AND

                (
                    :priority IS NULL
                    OR j.priority = :priority
                )

                AND

                (
                    :type IS NULL
                    OR j.type = :type
                )

                AND

                (
                    :scheduleType IS NULL
                    OR j.scheduleType = :scheduleType
                )
            """)
    Page<Job> findJobsWithFilters(
            @Param("search") String search,
            @Param("tag") String tag,
            @Param("status") JobStatus status,
            @Param("priority") JobPriority priority,
            @Param("type") JobType type,
            @Param("scheduleType") ScheduleType scheduleType,
            Pageable pageable
    );
}