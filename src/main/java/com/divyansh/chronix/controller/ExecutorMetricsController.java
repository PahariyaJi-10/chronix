package com.divyansh.chronix.controller;

import com.divyansh.chronix.dto.ExecutorMetricsResponse;
import com.divyansh.chronix.service.ExecutorMetricsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/system")
public class ExecutorMetricsController {

    private final ExecutorMetricsService executorMetricsService;

    public ExecutorMetricsController(
            ExecutorMetricsService executorMetricsService) {

        this.executorMetricsService = executorMetricsService;
    }

    @GetMapping("/executor")
    public ExecutorMetricsResponse getExecutorMetrics() {

        return executorMetricsService.getExecutorMetrics();
    }
}