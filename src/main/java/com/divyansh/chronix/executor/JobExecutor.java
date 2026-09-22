package com.divyansh.chronix.executor;

import com.divyansh.chronix.entity.DeadLetterJob;
import com.divyansh.chronix.entity.Job;
import com.divyansh.chronix.entity.JobAuditAction;
import com.divyansh.chronix.entity.JobExecution;
import com.divyansh.chronix.entity.JobStatus;
import com.divyansh.chronix.entity.ScheduleType;
import com.divyansh.chronix.repository.DeadLetterJobRepository;
import com.divyansh.chronix.repository.JobExecutionRepository;
import com.divyansh.chronix.repository.JobRepository;
import com.divyansh.chronix.service.JobAuditLogService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class JobExecutor {

    private static final int MAX_RETRIES = 3;

    private final JobRepository jobRepository;
    private final JobExecutionRepository jobExecutionRepository;
    private final DeadLetterJobRepository deadLetterJobRepository;
    private final JobAuditLogService jobAuditLogService;

    @Value("${chronix.executor.job-timeout-seconds}")
    private long timeoutSeconds;

    private final ExecutorService jobWorkExecutor =
            Executors.newCachedThreadPool();

    public JobExecutor(
            JobRepository jobRepository,
            JobExecutionRepository jobExecutionRepository,
            DeadLetterJobRepository deadLetterJobRepository,
            JobAuditLogService jobAuditLogService) {

        this.jobRepository = jobRepository;
        this.jobExecutionRepository = jobExecutionRepository;
        this.deadLetterJobRepository = deadLetterJobRepository;
        this.jobAuditLogService = jobAuditLogService;
    }

    @Async("chronixTaskExecutor")
    public void execute(Job job) {

        JobExecution execution = new JobExecution();

        execution.setJob(job);
        execution.setStatus(JobStatus.RUNNING);
        execution.setStartedAt(LocalDateTime.now());
        execution.setAttemptNumber(job.getRetryCount() + 1);

        jobExecutionRepository.save(execution);

        jobAuditLogService.log(
                job,
                JobAuditAction.JOB_STARTED,
                "Job execution started. Attempt: "
                        + execution.getAttemptNumber()
        );

        System.out.println(
                "Executing Job: " + job.getName()
                        + " | Attempt: " + execution.getAttemptNumber()
                        + " | Schedule: " + job.getScheduleType()
                        + " | Thread: " + Thread.currentThread().getName()
        );

        Future<?> future = null;

        try {

            future = jobWorkExecutor.submit(() -> {

                try {

                    /*
                     * Simulated job processing.
                     *
                     * This represents the actual work performed
                     * by a Chronix job.
                     */
                    Thread.sleep(5000);

                    if (job.getPayload() != null
                            && job.getPayload().equalsIgnoreCase("FAIL")) {

                        throw new RuntimeException(
                                "Simulated job failure"
                        );
                    }

                } catch (InterruptedException e) {

                    Thread.currentThread().interrupt();

                    throw new RuntimeException(
                            "Job execution interrupted",
                            e
                    );
                }
            });

            /*
             * Wait for the actual job work to finish.
             * If it exceeds the configured timeout,
             * TimeoutException is thrown.
             */
            future.get(
                    timeoutSeconds,
                    TimeUnit.SECONDS
            );

            execution.setStatus(JobStatus.COMPLETED);

            jobAuditLogService.log(
                    job,
                    JobAuditAction.JOB_COMPLETED,
                    "Job execution completed successfully"
            );

            System.out.println(
                    "Completed Job: "
                            + job.getName()
            );

            scheduleNextExecution(job);

        } catch (TimeoutException e) {

            if (future != null) {
                future.cancel(true);
            }

            handleFailure(
                    job,
                    execution,
                    "Job execution timed out after "
                            + timeoutSeconds
                            + " seconds"
            );

        } catch (ExecutionException e) {

            Throwable cause = e.getCause();

            String errorMessage =
                    cause != null && cause.getMessage() != null
                            ? cause.getMessage()
                            : "Job execution failed";

            handleFailure(
                    job,
                    execution,
                    errorMessage
            );

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            handleFailure(
                    job,
                    execution,
                    "Job executor thread was interrupted"
            );

        } catch (Exception e) {

            handleFailure(
                    job,
                    execution,
                    e.getMessage() != null
                            ? e.getMessage()
                            : "Unexpected job execution error"
            );

        } finally {

            LocalDateTime finishedAt =
                    LocalDateTime.now();

            execution.setFinishedAt(finishedAt);

            job.setUpdatedAt(finishedAt);

            jobRepository.save(job);
            jobExecutionRepository.save(execution);
        }
    }

    private void handleFailure(
            Job job,
            JobExecution execution,
            String errorMessage) {

        int retries =
                job.getRetryCount() + 1;

        job.setRetryCount(retries);

        execution.setStatus(JobStatus.FAILED);
        execution.setErrorMessage(errorMessage);

        jobAuditLogService.log(
                job,
                JobAuditAction.JOB_FAILED,
                "Job execution failed. Attempt: "
                        + retries
                        + " | Error: "
                        + errorMessage
        );

        if (retries < MAX_RETRIES) {

            job.setStatus(JobStatus.PENDING);

            System.out.println(
                    "Retry "
                            + retries
                            + " scheduled for: "
                            + job.getName()
            );

        } else {

            job.setStatus(JobStatus.FAILED);

            System.out.println(
                    "Job permanently failed: "
                            + job.getName()
            );

            if (!deadLetterJobRepository.existsByJobId(
                    job.getId())) {

                DeadLetterJob deadLetterJob =
                        new DeadLetterJob();

                deadLetterJob.setJob(job);
                deadLetterJob.setJobName(job.getName());
                deadLetterJob.setAttemptCount(retries);
                deadLetterJob.setErrorMessage(errorMessage);
                deadLetterJob.setPayload(job.getPayload());
                deadLetterJob.setFailedAt(
                        LocalDateTime.now()
                );

                deadLetterJobRepository.save(
                        deadLetterJob
                );

                jobAuditLogService.log(
                        job,
                        JobAuditAction.JOB_MOVED_TO_DLQ,
                        "Job moved to Dead-Letter Queue after "
                                + retries
                                + " failed attempts"
                );

                System.out.println(
                        "Job moved to Dead-Letter Queue: "
                                + job.getName()
                );
            }
        }
    }

    private void scheduleNextExecution(Job job) {

        ScheduleType scheduleType =
                job.getScheduleType();

        if (scheduleType == null
                || scheduleType == ScheduleType.ONE_TIME) {

            job.setStatus(JobStatus.COMPLETED);
            return;
        }

        LocalDateTime currentScheduledTime =
                job.getScheduledAt();

        LocalDateTime nextScheduledTime;

        switch (scheduleType) {

            case HOURLY:

                nextScheduledTime =
                        currentScheduledTime.plusHours(1);

                break;

            case DAILY:

                nextScheduledTime =
                        currentScheduledTime.plusDays(1);

                break;

            case CRON:

                if (job.getCronExpression() == null
                        || job.getCronExpression().isBlank()) {

                    throw new IllegalArgumentException(
                            "Cron expression is required for CRON schedule"
                    );
                }

                CronExpression cron =
                        CronExpression.parse(
                                job.getCronExpression()
                        );

                nextScheduledTime =
                        cron.next(currentScheduledTime);

                if (nextScheduledTime == null) {

                    throw new IllegalArgumentException(
                            "Unable to calculate next execution time"
                    );
                }

                break;

            default:

                job.setStatus(JobStatus.COMPLETED);
                return;
        }

        job.setScheduledAt(nextScheduledTime);
        job.setRetryCount(0);
        job.setStatus(JobStatus.PENDING);

        System.out.println(
                "Next execution scheduled for: "
                        + job.getName()
                        + " | Next run: "
                        + nextScheduledTime
        );
    }
}