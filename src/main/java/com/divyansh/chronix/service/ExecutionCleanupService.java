package com.divyansh.chronix.service;

import com.divyansh.chronix.entity.JobExecution;
import com.divyansh.chronix.repository.JobExecutionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ExecutionCleanupService {

    private final JobExecutionRepository jobExecutionRepository;

    @Value("${chronix.execution.retention-days:30}")
    private int retentionDays;

    public ExecutionCleanupService(
            JobExecutionRepository jobExecutionRepository) {

        this.jobExecutionRepository = jobExecutionRepository;
    }

    // Automatic cleanup every day at 2:00 AM
    @Scheduled(cron = "0 0 2 * * *")
    public void cleanupOldExecutions() {

        performCleanup();
    }

    // Perform execution cleanup
    public int performCleanup() {

        LocalDateTime cutoffDate =
                LocalDateTime.now().minusDays(retentionDays);

        List<JobExecution> oldExecutions =
                jobExecutionRepository
                        .findByFinishedAtBefore(cutoffDate);

        if (oldExecutions.isEmpty()) {

            System.out.println(
                    "Execution cleanup: No old executions found."
            );

            return 0;
        }

        int deletedCount = oldExecutions.size();

        jobExecutionRepository.deleteAll(oldExecutions);

        System.out.println(
                "Execution cleanup: Deleted "
                        + deletedCount
                        + " executions older than "
                        + retentionDays
                        + " days."
        );

        return deletedCount;
    }
}