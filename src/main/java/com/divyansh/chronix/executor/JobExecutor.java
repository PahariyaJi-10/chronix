package com.divyansh.chronix.executor;

import com.divyansh.chronix.entity.DeadLetterJob;
import com.divyansh.chronix.entity.Job;
import com.divyansh.chronix.entity.JobExecution;
import com.divyansh.chronix.entity.JobStatus;
import com.divyansh.chronix.repository.DeadLetterJobRepository;
import com.divyansh.chronix.repository.JobExecutionRepository;
import com.divyansh.chronix.repository.JobRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class JobExecutor {

    private static final int MAX_RETRIES = 3;

    private final JobRepository jobRepository;
    private final JobExecutionRepository jobExecutionRepository;
    private final DeadLetterJobRepository deadLetterJobRepository;

    public JobExecutor(
            JobRepository jobRepository,
            JobExecutionRepository jobExecutionRepository,
            DeadLetterJobRepository deadLetterJobRepository) {

        this.jobRepository = jobRepository;
        this.jobExecutionRepository = jobExecutionRepository;
        this.deadLetterJobRepository = deadLetterJobRepository;
    }

    @Async("chronixTaskExecutor")
    public void execute(Job job) {

        JobExecution execution = new JobExecution();

        execution.setJob(job);
        execution.setStatus(JobStatus.RUNNING);
        execution.setStartedAt(LocalDateTime.now());
        execution.setAttemptNumber(job.getRetryCount() + 1);

        // Job is already RUNNING because the scheduler claimed it.
        jobExecutionRepository.save(execution);

        System.out.println(
                "Executing Job: " + job.getName()
                        + " | Attempt: " + execution.getAttemptNumber()
                        + " | Thread: " + Thread.currentThread().getName()
        );

        try {

            Thread.sleep(5000);

            if (job.getPayload() != null
                    && job.getPayload().equalsIgnoreCase("FAIL")) {

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

            if (retries < MAX_RETRIES) {

                job.setStatus(JobStatus.PENDING);

                System.out.println(
                        "Retry " + retries
                                + " scheduled for: "
                                + job.getName()
                );

            } else {

                job.setStatus(JobStatus.FAILED);

                System.out.println(
                        "Job permanently failed: "
                                + job.getName()
                );

                // Move permanently failed job to Dead-Letter Queue
                if (!deadLetterJobRepository.existsByJobId(job.getId())) {

                    DeadLetterJob deadLetterJob = new DeadLetterJob();

                    deadLetterJob.setJob(job);
                    deadLetterJob.setJobName(job.getName());
                    deadLetterJob.setAttemptCount(retries);
                    deadLetterJob.setErrorMessage(e.getMessage());
                    deadLetterJob.setPayload(job.getPayload());
                    deadLetterJob.setFailedAt(LocalDateTime.now());

                    deadLetterJobRepository.save(deadLetterJob);

                    System.out.println(
                            "Job moved to Dead-Letter Queue: "
                                    + job.getName()
                    );
                }
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