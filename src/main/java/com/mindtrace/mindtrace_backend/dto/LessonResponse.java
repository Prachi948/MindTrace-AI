package com.mindtrace.mindtrace_backend.dto;

public record LessonResponse(
        Long id,
        String title,
        String content,
        Integer sequenceNumber,
        Long moduleId
) {
}