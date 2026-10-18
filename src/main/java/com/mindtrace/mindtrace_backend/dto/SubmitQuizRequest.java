package com.mindtrace.mindtrace_backend.dto;

import java.util.Map;

public record SubmitQuizRequest(Map<Long, String> answers) {
}
