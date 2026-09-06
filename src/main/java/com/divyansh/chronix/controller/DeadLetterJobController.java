package com.divyansh.chronix.controller;

import com.divyansh.chronix.dto.DeadLetterJobResponse;
import com.divyansh.chronix.service.DeadLetterJobService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dead-letter-jobs")
public class DeadLetterJobController {

    private final DeadLetterJobService deadLetterJobService;

    public DeadLetterJobController(
            DeadLetterJobService deadLetterJobService) {
        this.deadLetterJobService = deadLetterJobService;
    }

    @GetMapping
    public List<DeadLetterJobResponse> getAllDeadLetterJobs() {

        return deadLetterJobService
                .getAllDeadLetterJobs();
    }
}