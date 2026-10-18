package com.mindtrace.mindtrace_backend.controller;
import com.mindtrace.mindtrace_backend.dto.*;
import com.mindtrace.mindtrace_backend.service.ProgressService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/progress")
public class ProgressController {
    private final ProgressService service;
    public ProgressController(ProgressService service) { this.service = service; }

    @PutMapping("/users/{userId}/lessons/{lessonId}")
    @PreAuthorize("#p0 == authentication.principal")
    public ProgressResponse update(@PathVariable Long userId, @PathVariable Long lessonId,
                                   @RequestBody ProgressRequest request) {
        return service.update(userId, lessonId, request);
    }
    @GetMapping("/users/{userId}")
    @PreAuthorize("#p0 == authentication.principal")
    public List<ProgressResponse> list(@PathVariable Long userId) { return service.list(userId); }

    @GetMapping("/users/{userId}/lessons/{lessonId}")
    @PreAuthorize("#p0 == authentication.principal")
    public ProgressResponse get(@PathVariable Long userId, @PathVariable Long lessonId) {
        return service.get(userId, lessonId);
    }
    @GetMapping("/users/{userId}/summary")
    @PreAuthorize("#p0 == authentication.principal")
    public ProgressSummaryResponse summary(@PathVariable Long userId) { return service.summary(userId); }
}
