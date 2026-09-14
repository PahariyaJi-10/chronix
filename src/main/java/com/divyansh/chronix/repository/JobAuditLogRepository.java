package com.divyansh.chronix.repository;

import com.divyansh.chronix.entity.JobAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobAuditLogRepository
        extends JpaRepository<JobAuditLog, Long> {

    List<JobAuditLog> findAllByOrderByCreatedAtDesc();

    List<JobAuditLog> findByJobIdOrderByCreatedAtDesc(Long jobId);
}