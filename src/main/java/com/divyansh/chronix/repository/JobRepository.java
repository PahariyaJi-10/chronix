package com.divyansh.chronix.repository;

import com.divyansh.chronix.entity.Job;
import com.divyansh.chronix.entity.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findByStatusAndScheduledAtLessThanEqual(
            JobStatus status,
            LocalDateTime scheduledAt
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