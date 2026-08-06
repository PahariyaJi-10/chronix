package com.divyansh.chronix.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class JobScheduler {

    @Scheduled(fixedRate = 10000)
    public void executeJobs() {

        System.out.println("Scheduler is checking for pending jobs...");
    }
}