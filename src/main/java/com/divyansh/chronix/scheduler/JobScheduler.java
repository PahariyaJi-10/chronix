package com.divyansh.chronix.scheduler;

import com.divyansh.chronix.entity.Job;
import com.divyansh.chronix.entity.JobStatus;
import com.divyansh.chronix.executor.JobExecutor;
import com.divyansh.chronix.repository.JobRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class JobScheduler {

    private final JobRepository jobRepository;
    private final JobExecutor jobExecutor;

    public JobScheduler(
            JobRepository jobRepository,
            JobExecutor jobExecutor) {

        this.jobRepository = jobRepository;
        this.jobExecutor = jobExecutor;
    }

    @Scheduled(fixedRate = 10000)
    public void scheduleJobs() {

        List<Job> pendingJobs =
                jobRepository.findByStatusAndScheduledAtLessThanEqual(
                        JobStatus.PENDING,
                        LocalDateTime.now()
                );

        for (Job job : pendingJobs) {

            job.setStatus(JobStatus.RUNNING);
            job.setUpdatedAt(LocalDateTime.now());

            jobRepository.save(job);

            jobExecutor.execute(job);
        }
    }
}