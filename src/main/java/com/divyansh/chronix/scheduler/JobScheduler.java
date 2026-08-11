package com.divyansh.chronix.scheduler;

import com.divyansh.chronix.entity.Job;
import com.divyansh.chronix.service.JobSchedulerService;
import com.divyansh.chronix.executor.JobExecutor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JobScheduler {

    private final JobSchedulerService jobSchedulerService;
    private final JobExecutor jobExecutor;

    public JobScheduler(
            JobSchedulerService jobSchedulerService,
            JobExecutor jobExecutor) {

        this.jobSchedulerService = jobSchedulerService;
        this.jobExecutor = jobExecutor;
    }

    @Scheduled(fixedRate = 10000)
    public void scheduleJobs() {

        List<Job> jobs = jobSchedulerService.claimDueJobs();

        if (jobs.isEmpty()) {
            System.out.println("No pending jobs found.");
            return;
        }

        for (Job job : jobs) {

            System.out.println(
                    "Job claimed for execution: " + job.getName()
            );

            jobExecutor.execute(job);
        }
    }
}