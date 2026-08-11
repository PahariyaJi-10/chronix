package com.divyansh.chronix.service;

import com.divyansh.chronix.entity.Job;
import com.divyansh.chronix.entity.JobStatus;
import com.divyansh.chronix.repository.JobRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class JobSchedulerService {

    private final JobRepository jobRepository;

    public JobSchedulerService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Transactional
    public List<Job> claimDueJobs() {

        List<Job> jobs = jobRepository.findDueJobsForUpdate(
                JobStatus.PENDING,
                LocalDateTime.now()
        );

        for (Job job : jobs) {

            job.setStatus(JobStatus.RUNNING);
            job.setUpdatedAt(LocalDateTime.now());
        }

        jobRepository.saveAll(jobs);

        return jobs;
    }
}