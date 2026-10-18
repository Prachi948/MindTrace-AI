package com.mindtrace.mindtrace_backend.dto;

public record QuestionResponse(Long id, String questionText, String optionA, String optionB, String optionC,
                               String optionD) {
}
