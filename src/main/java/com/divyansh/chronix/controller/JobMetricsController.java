package com.divyansh.chronix.controller;

import com.divyansh.chronix.dto.JobMetricsResponse;
import com.divyansh.chronix.service.JobMetricsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/metrics")
public class JobMetricsController {

    private final JobMetricsService jobMetricsService;

    public JobMetricsController(
            JobMetricsService jobMetricsService) {

        this.jobMetricsService = jobMetricsService;
    }

    @GetMapping
    public JobMetricsResponse getMetrics() {

        return jobMetricsService.getMetrics();
    }
}