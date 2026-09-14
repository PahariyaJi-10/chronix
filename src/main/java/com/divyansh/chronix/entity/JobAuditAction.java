package com.divyansh.chronix.entity;

public enum JobAuditAction {

    JOB_CREATED,
    JOB_UPDATED,
    JOB_CANCELLED,
    JOB_PAUSED,
    JOB_RESUMED,
    JOB_RETRY_REQUESTED,
    JOB_STARTED,
    JOB_COMPLETED,
    JOB_FAILED,
    JOB_MOVED_TO_DLQ,
    JOB_REPROCESSED
}