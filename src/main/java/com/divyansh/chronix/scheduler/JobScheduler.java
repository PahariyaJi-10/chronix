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
                jobRepository.findDueJobsForUpdate(
                        JobStatus.PENDING,
                        now
                );

        for (Job job : jobs) {

            // Check job dependency
            if (job.getDependsOn() != null) {

                Job dependency = job.getDependsOn();

                // Cancel job if dependency permanently failed
                // or was cancelled
                if (dependency.getStatus() == JobStatus.FAILED
                        || dependency.getStatus() == JobStatus.CANCELLED) {

                    job.setStatus(JobStatus.CANCELLED);
                    job.setUpdatedAt(now);

                    jobRepository.save(job);

                    System.out.println(
                            "Job cancelled because dependency failed: "
                                    + job.getName()
                                    + " | Depends on: "
                                    + dependency.getName()
                                    + " | Dependency Status: "
                                    + dependency.getStatus()
                    );

                    continue;
                }

                // Wait until dependency is completed
                if (dependency.getStatus() != JobStatus.COMPLETED) {

                    System.out.println(
                            "Job waiting for dependency: "
                                    + job.getName()
                                    + " | Depends on: "
                                    + dependency.getName()
                                    + " | Dependency Status: "
                                    + dependency.getStatus()
                    );

                    continue;
                }
            }

            // Atomically claim the job
            int claimed = jobRepository.claimJob(
                    job.getId(),
                    JobStatus.PENDING,
                    JobStatus.RUNNING,
                    now
            );

            if (claimed == 1) {

                System.out.println(
                        "Scheduler claimed job: "
                                + job.getName()
                                + " | Priority: "
                                + job.getPriority()
                );

                jobExecutor.execute(job);

            } else {

                System.out.println(
                        "Job already claimed: "
                                + job.getName()
                );
            }
        }
    }
}