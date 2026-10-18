package com.mindtrace.mindtrace_backend.dto;

public record QuestionRequest(String questionText, String optionA, String optionB, String optionC, String optionD,
                              String correctAnswer, String explanation) {
}
