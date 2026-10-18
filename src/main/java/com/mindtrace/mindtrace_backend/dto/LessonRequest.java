package com.mindtrace.mindtrace_backend.dto;

public record LessonRequest(
        String title,
        String content,
        Integer sequenceNumber
) {
}