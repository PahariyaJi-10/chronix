package com.divyansh.chronix.executor;

import com.divyansh.chronix.entity.Job;
import com.divyansh.chronix.entity.JobExecution;
import com.divyansh.chronix.entity.JobStatus;
import com.divyansh.chronix.repository.JobExecutionRepository;
import com.divyansh.chronix.repository.JobRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class JobExecutor {

    private final JobRepository jobRepository;
    private final JobExecutionRepository jobExecutionRepository;

    public JobExecutor(
            JobRepository jobRepository,
            JobExecutionRepository jobExecutionRepository) {

        this.jobRepository = jobRepository;
        this.jobExecutionRepository = jobExecutionRepository;
    }

    @Async
    public void execute(Job job) {

        JobExecution execution = new JobExecution();

        execution.setJob(job);
        execution.setStatus(JobStatus.RUNNING);
        execution.setStartedAt(LocalDateTime.now());
        execution.setAttemptNumber(job.getRetryCount() + 1);

        job.setStatus(JobStatus.RUNNING);
        job.setUpdatedAt(LocalDateTime.now());

        jobRepository.save(job);
        jobExecutionRepository.save(execution);

        System.out.println(
                "Executing Job: " + job.getName()
                        + " | Attempt: " + execution.getAttemptNumber()
                        + " | Thread: " + Thread.currentThread().getName()
        );

        try {

            // Simulate job execution
            Thread.sleep(5000);

            // Simulate failure
            if (job.getPayload() != null &&
                    job.getPayload().equalsIgnoreCase("FAIL")) {

                throw new RuntimeException("Simulated job failure");
            }

            job.setStatus(JobStatus.COMPLETED);
            execution.setStatus(JobStatus.COMPLETED);

            System.out.println(
                    "Completed Job: " + job.getName()
            );

        } catch (Exception e) {

            int retries = job.getRetryCount() + 1;
            job.setRetryCount(retries);

            execution.setStatus(JobStatus.FAILED);
            execution.setErrorMessage(e.getMessage());

            if (retries < 3) {

                job.setStatus(JobStatus.PENDING);

                System.out.println(
                        "Retry " + retries +
                        " scheduled for: " + job.getName()
                );

            } else {

                job.setStatus(JobStatus.FAILED);

                System.out.println(
                        "Job permanently failed: " + job.getName()
                );
            }

        } finally {

            LocalDateTime finishedAt = LocalDateTime.now();

            execution.setFinishedAt(finishedAt);

            job.setUpdatedAt(finishedAt);

            jobRepository.save(job);
            jobExecutionRepository.save(execution);
        }
    }
}