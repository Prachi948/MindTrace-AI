package com.mindtrace.mindtrace_backend.dto;
import java.time.LocalDateTime;
public record ProgressResponse(Long id, Long userId, Long lessonId, String lessonTitle,
 Integer progressPercentage, Boolean completed, LocalDateTime startedAt, LocalDateTime completedAt) {}
