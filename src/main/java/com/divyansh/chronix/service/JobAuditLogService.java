package com.divyansh.chronix.service;

import com.divyansh.chronix.dto.JobAuditLogResponse;
import com.divyansh.chronix.entity.Job;
import com.divyansh.chronix.entity.JobAuditAction;
import com.divyansh.chronix.entity.JobAuditLog;
import com.divyansh.chronix.repository.JobAuditLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobAuditLogService {

    private final JobAuditLogRepository jobAuditLogRepository;

    public JobAuditLogService(
            JobAuditLogRepository jobAuditLogRepository) {

        this.jobAuditLogRepository = jobAuditLogRepository;
    }

    public void log(
            Job job,
            JobAuditAction action,
            String message) {

        JobAuditLog auditLog = new JobAuditLog();

        auditLog.setJob(job);
        auditLog.setAction(action);
        auditLog.setMessage(message);
        auditLog.setCreatedAt(LocalDateTime.now());

        jobAuditLogRepository.save(auditLog);
    }

    public List<JobAuditLogResponse> getAllLogs() {

        return jobAuditLogRepository
                .findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<JobAuditLogResponse> getLogsByJobId(Long jobId) {

        return jobAuditLogRepository
                .findByJobIdOrderByCreatedAtDesc(jobId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private JobAuditLogResponse toResponse(
            JobAuditLog auditLog) {

        return new JobAuditLogResponse(
                auditLog.getId(),
                auditLog.getJob().getId(),
                auditLog.getJob().getName(),
                auditLog.getAction(),
                auditLog.getMessage(),
                auditLog.getCreatedAt()
        );
    }
}