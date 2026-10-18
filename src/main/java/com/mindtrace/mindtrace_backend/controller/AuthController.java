package com.mindtrace.mindtrace_backend.controller;

import com.mindtrace.mindtrace_backend.dto.AuthResponse;
import com.mindtrace.mindtrace_backend.dto.LoginRequest;
import com.mindtrace.mindtrace_backend.dto.SignupRequest;
import com.mindtrace.mindtrace_backend.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public AuthResponse signup(@RequestBody SignupRequest request) {
        return authService.signup(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
