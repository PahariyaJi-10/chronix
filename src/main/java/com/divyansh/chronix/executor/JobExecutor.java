package com.divyansh.chronix.executor;

import com.divyansh.chronix.entity.Job;
import com.divyansh.chronix.entity.JobStatus;
import com.divyansh.chronix.repository.JobRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class JobExecutor {

    private final JobRepository jobRepository;

    public JobExecutor(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Async
    public void execute(Job job) {

        System.out.println(
                "Executing Job: " + job.getName()
                        + " | Thread: " + Thread.currentThread().getName()
        );

        try {
            job.setStatus(JobStatus.RUNNING);
            job.setUpdatedAt(LocalDateTime.now());
            jobRepository.save(job);

            // Simulate job execution
            Thread.sleep(5000);

            if (job.getPayload() != null &&
                    job.getPayload().equalsIgnoreCase("FAIL")) {

                throw new RuntimeException("Simulated job failure");
            }

            job.setStatus(JobStatus.COMPLETED);

            System.out.println("Completed Job: " + job.getName());

        } catch (Exception e) {

            int retries = job.getRetryCount() + 1;
            job.setRetryCount(retries);

            if (retries < 3) {
                job.setStatus(JobStatus.PENDING);
                System.out.println("Retry " + retries + " scheduled for: " + job.getName());
            } else {
                job.setStatus(JobStatus.FAILED);
                System.out.println("Job permanently failed: " + job.getName());
            }

        } finally {
            job.setUpdatedAt(LocalDateTime.now());
            jobRepository.save(job);
        }
    }
}