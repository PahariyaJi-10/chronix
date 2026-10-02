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

    // Basic filters

    List<Job> findByStatus(JobStatus status);

    List<Job> findByPriority(JobPriority priority);

    List<Job> findByType(JobType type);

    List<Job> findByScheduleType(ScheduleType scheduleType);

    // Combined filters

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

    // Pagination

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

    // Pagination + combined filters

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

    // Search by job name

    Page<Job> findByNameContainingIgnoreCase(
            String name,
            Pageable pageable
    );

    // Search + status

    Page<Job> findByNameContainingIgnoreCaseAndStatus(
            String name,
            JobStatus status,
            Pageable pageable
    );

    // Search + priority

    Page<Job> findByNameContainingIgnoreCaseAndPriority(
            String name,
            JobPriority priority,
            Pageable pageable
    );

    // Search + type

    Page<Job> findByNameContainingIgnoreCaseAndType(
            String name,
            JobType type,
            Pageable pageable
    );

    // Search + schedule type

    Page<Job> findByNameContainingIgnoreCaseAndScheduleType(
            String name,
            ScheduleType scheduleType,
            Pageable pageable
    );

    // Search by job tag

    Page<Job> findByTagsContainingIgnoreCase(
            String tag,
            Pageable pageable
    );

    // Dynamic filtering:
    // Search + Tag + Status + Priority + Type + Schedule Type
    //
    // Any parameter can be null, meaning that filter is ignored.

    @Query("""
            SELECT j
            FROM Job j
            WHERE
                (
                    :search IS NULL
                    OR LOWER(j.name) LIKE LOWER(CONCAT('%', :search, '%'))
                )
                AND
                (
                    :tag IS NULL
                    OR LOWER(
                        CONCAT(
                            ',',
                            COALESCE(j.tags, ''),
                            ','
                        )
                    ) LIKE CONCAT('%,', LOWER(:tag), ',%')
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