package com.divyansh.chronix.controller;

import com.divyansh.chronix.service.ExecutionCleanupService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/executions")
public class ExecutionCleanupController {

    private final ExecutionCleanupService executionCleanupService;

    public ExecutionCleanupController(
            ExecutionCleanupService executionCleanupService) {

        this.executionCleanupService = executionCleanupService;
    }

    // Manually trigger execution history cleanup
    @PostMapping("/cleanup")
    public Map<String, Object> cleanupExecutions() {

        int deletedCount =
                executionCleanupService.performCleanup();

        return Map.of(
                "message", "Execution cleanup completed",
                "deletedExecutions", deletedCount
        );
    }
}