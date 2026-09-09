package com.divyansh.chronix.exception;

import com.divyansh.chronix.dto.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(JobNotFoundException.class)
    public ApiErrorResponse handleJobNotFound(
            JobNotFoundException exception,
            HttpServletRequest request) {

        return new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "JOB_NOT_FOUND",
                exception.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(RuntimeException.class)
    public ApiErrorResponse handleRuntimeException(
            RuntimeException exception,
            HttpServletRequest request) {

        return new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "BAD_REQUEST",
                exception.getMessage(),
                request.getRequestURI()
        );
    }
}