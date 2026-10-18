package com.mindtrace.mindtrace_backend.controller;
import com.mindtrace.mindtrace_backend.dto.*;
import com.mindtrace.mindtrace_backend.service.GamificationService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/gamification")
public class GamificationController {
    private final GamificationService service;
    public GamificationController(GamificationService service) { this.service = service; }

    @PostMapping("/users/{userId}/rewards")
    @PreAuthorize("#p0 == authentication.principal")
    public RewardResponse award(@PathVariable Long userId, @RequestParam String code,
                                @RequestParam String title, @RequestParam(defaultValue = "0") int points) {
        return service.award(userId, code, title, points);
    }

    @GetMapping("/users/{userId}/rewards")
    @PreAuthorize("#p0 == authentication.principal")
    public List<RewardResponse> list(@PathVariable Long userId) { return service.list(userId); }

    @GetMapping("/users/{userId}/points")
    @PreAuthorize("#p0 == authentication.principal")
    public PointsSummaryResponse summary(@PathVariable Long userId) { return service.summary(userId); }
}
