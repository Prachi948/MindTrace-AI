package com.mindtrace.mindtrace_backend.dto;
public record ProgressSummaryResponse(Long userId, long lessonsStarted, long lessonsCompleted,
 double completionPercentage) {}
