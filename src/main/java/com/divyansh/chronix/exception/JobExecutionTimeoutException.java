package com.divyansh.chronix.exception;

public class JobExecutionTimeoutException extends RuntimeException {

    public JobExecutionTimeoutException(String message) {
        super(message);
    }
}