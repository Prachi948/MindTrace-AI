package com.mindtrace.mindtrace_backend.controller;
import com.mindtrace.mindtrace_backend.dto.LearningAnalyticsResponse;
import com.mindtrace.mindtrace_backend.service.LearningAnalyticsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analytics")
public class LearningAnalyticsController {
    private final LearningAnalyticsService service;
    public LearningAnalyticsController(LearningAnalyticsService service) { this.service = service; }

    @GetMapping("/users/{userId}")
    @PreAuthorize("#p0 == authentication.principal")
    public LearningAnalyticsResponse get(@PathVariable Long userId) { return service.get(userId); }
}
