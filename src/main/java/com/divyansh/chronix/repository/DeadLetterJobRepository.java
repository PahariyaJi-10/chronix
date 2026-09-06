package com.divyansh.chronix.repository;

import com.divyansh.chronix.entity.DeadLetterJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeadLetterJobRepository
        extends JpaRepository<DeadLetterJob, Long> {

    List<DeadLetterJob> findAllByOrderByFailedAtDesc();

    boolean existsByJobId(Long jobId);
}