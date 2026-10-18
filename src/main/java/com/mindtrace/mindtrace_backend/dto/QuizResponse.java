package com.mindtrace.mindtrace_backend.dto;

public record QuizResponse(Long id, String title, String description, Integer passingScore, Long lessonId) {
}
