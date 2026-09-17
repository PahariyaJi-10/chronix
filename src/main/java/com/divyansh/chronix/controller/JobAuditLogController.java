package com.divyansh.chronix.controller;

import com.divyansh.chronix.dto.JobAuditLogResponse;
import com.divyansh.chronix.service.JobAuditLogService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit-logs")
public class JobAuditLogController {

    private final JobAuditLogService jobAuditLogService;

    public JobAuditLogController(
            JobAuditLogService jobAuditLogService) {

        this.jobAuditLogService = jobAuditLogService;
    }

    @GetMapping
    public List<JobAuditLogResponse> getAllLogs() {

        return jobAuditLogService.getAllLogs();
    }

    @GetMapping("/jobs/{jobId}")
    public List<JobAuditLogResponse> getLogsByJobId(
            @PathVariable Long jobId) {

        return jobAuditLogService
                .getLogsByJobId(jobId);
    }
}