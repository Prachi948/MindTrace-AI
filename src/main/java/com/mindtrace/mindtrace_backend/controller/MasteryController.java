package com.mindtrace.mindtrace_backend.controller;
import com.mindtrace.mindtrace_backend.dto.*;
import com.mindtrace.mindtrace_backend.service.MasteryService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/mastery")
public class MasteryController {
    private final MasteryService service;
    public MasteryController(MasteryService service) { this.service = service; }

    @PutMapping("/users/{userId}/concepts/{conceptId}")
    @PreAuthorize("#p0 == authentication.principal")
    public MasteryResponse update(@PathVariable Long userId, @PathVariable Long conceptId,
                                  @RequestBody MasteryRequest request) {
        return service.upsert(userId, conceptId, request);
    }

    @GetMapping("/users/{userId}")
    @PreAuthorize("#p0 == authentication.principal")
    public List<MasteryResponse> list(@PathVariable Long userId) { return service.list(userId); }
}
