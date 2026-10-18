package com.mindtrace.mindtrace_backend.dto;

public record QuizResultResponse(Long attemptId, Integer score, Integer totalQuestions, Double percentage,
                                 Boolean passed) {
}
