package com.mindtrace.mindtrace_backend.dto;

public record AuthResponse(Long userId, String username, String email, String role,
                           String accessToken, String message) {}
