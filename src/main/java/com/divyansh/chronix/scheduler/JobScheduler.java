package com.divyansh.chronix.scheduler;

import com.divyansh.chronix.entity.Job;
import com.divyansh.chronix.entity.JobStatus;
import com.divyansh.chronix.executor.JobExecutor;
import com.divyansh.chronix.repository.JobRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public void checkScheduledJobs() {

        LocalDateTime now = LocalDateTime.now();

        List<Job> jobs =
                jobRepository.findByStatusAndScheduledAtLessThanEqual(
                        JobStatus.PENDING,
                        now
                );

        for (Job job : jobs) {

            int claimed = jobRepository.claimJob(
                    job.getId(),
                    JobStatus.PENDING,
                    JobStatus.RUNNING,
                    now
            );

            if (claimed == 1) {

                System.out.println(
                        "Scheduler claimed job: " + job.getName()
                );

                jobExecutor.execute(job);

            } else {

                System.out.println(
                        "Job already claimed: " + job.getName()
                );
            }
        }
    }
}