package com.divyansh.chronix.scheduler;

import com.divyansh.chronix.entity.Job;
import com.divyansh.chronix.entity.JobStatus;
import com.divyansh.chronix.repository.JobRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class JobScheduler {
private static final int MAX_RETRY = 3;
    private final JobRepository jobRepository;

    public JobScheduler(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Scheduled(fixedRate = 10000)
    public void executeJobs() {

        List<Job> pendingJobs =
                jobRepository.findByStatusAndScheduledAtLessThanEqual(
                        JobStatus.PENDING,
                        LocalDateTime.now()
                );

        if (pendingJobs.isEmpty()) {
            System.out.println("No pending jobs found.");
            return;
        }

        for (Job job : pendingJobs) {

            System.out.println("Executing Job: " + job.getName());

            job.setStatus(JobStatus.RUNNING);
            jobRepository.save(job);

           try {

    Thread.sleep(2000);

    // Simulate failure if payload contains "FAIL"
    if (job.getPayload() != null &&
            job.getPayload().equalsIgnoreCase("FAIL")) {

        throw new RuntimeException("Simulated job failure");
    }

    job.setStatus(JobStatus.COMPLETED);

    System.out.println("Job completed successfully.");

} catch (Exception e) {

    int retries = job.getRetryCount() + 1;
    job.setRetryCount(retries);

    if (retries < MAX_RETRY) {

        job.setStatus(JobStatus.PENDING);

        System.out.println("Retry " + retries + " scheduled.");

    } else {

        job.setStatus(JobStatus.FAILED);

        System.out.println("Job permanently failed.");
    }
}

            job.setUpdatedAt(LocalDateTime.now());

            jobRepository.save(job);

            System.out.println("Completed Job: " + job.getName());
        }
    }
}