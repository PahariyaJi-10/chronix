package com.divyansh.chronix.scheduler;

import com.divyansh.chronix.entity.JobExecution;
import com.divyansh.chronix.entity.JobStatus;
import com.divyansh.chronix.repository.JobExecutionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class JobTimeoutMonitor {

    private final JobExecutionRepository jobExecutionRepository;

    @Value("${chronix.executor.job-timeout-seconds}")
    private long timeoutSeconds;

    public JobTimeoutMonitor(
            JobExecutionRepository jobExecutionRepository) {

        this.jobExecutionRepository = jobExecutionRepository;
    }

    @Scheduled(fixedRate = 5000)
    public void monitorRunningExecutions() {

        LocalDateTime now = LocalDateTime.now();

        List<JobExecution> runningExecutions =
                jobExecutionRepository.findByStatus(
                        JobStatus.RUNNING
                );

        for (JobExecution execution : runningExecutions) {

            if (execution.getStartedAt() == null) {
                continue;
            }

            long runningSeconds =
                    Duration.between(
                            execution.getStartedAt(),
                            now
                    ).getSeconds();

            if (runningSeconds >= timeoutSeconds) {

                System.out.println(
                        "Execution exceeded configured timeout: "
                                + execution.getId()
                                + " | Job ID: "
                                + execution.getJob().getId()
                                + " | Running for: "
                                + runningSeconds
                                + " seconds"
                );
            }
        }
    }
}