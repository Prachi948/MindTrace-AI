package com.mindtrace.mindtrace_backend.service;

import com.mindtrace.mindtrace_backend.dto.AuthResponse;
import com.mindtrace.mindtrace_backend.dto.LoginRequest;
import com.mindtrace.mindtrace_backend.dto.SignupRequest;
import com.mindtrace.mindtrace_backend.entity.AppUser;
import com.mindtrace.mindtrace_backend.repository.AppUserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.regex.Pattern;

@Service
public class AuthService {
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final AppUserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(AppUserRepository userRepository,
                       BCryptPasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse signup(SignupRequest request) {
        if (request == null || blank(request.username()) ||
                blank(request.email()) || blank(request.password())) {
            throw new IllegalArgumentException("Username, email and password are required");
        }

        String username = request.username().trim();
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        String password = request.password();

        if (username.length() < 3 || username.length() > 100)
            throw new IllegalArgumentException("Username must be 3 to 100 characters");
        if (!EMAIL_PATTERN.matcher(email).matches())
            throw new IllegalArgumentException("Enter a valid email address");
        if (password.length() < 8 || password.length() > 72)
            throw new IllegalArgumentException("Password must be 8 to 72 characters");
        if (userRepository.existsByUsernameIgnoreCase(username))
            throw new IllegalArgumentException("Username is already registered");
        if (userRepository.existsByEmailIgnoreCase(email))
            throw new IllegalArgumentException("Email is already registered");

        AppUser user = new AppUser();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setRole("STUDENT");
        AppUser saved = userRepository.save(user);

        String token = jwtService.generateToken(saved.getId(), saved.getRole());
        return new AuthResponse(saved.getId(), saved.getUsername(), saved.getEmail(),
                saved.getRole(), token, "Signup successful");
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        if (request == null || blank(request.email()) || blank(request.password()))
            throw new IllegalArgumentException("Email and password are required");

        AppUser user = userRepository.findByEmailIgnoreCase(request.email().trim())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash()))
            throw new IllegalArgumentException("Invalid email or password");

        String token = jwtService.generateToken(user.getId(), user.getRole());
        return new AuthResponse(user.getId(), user.getUsername(), user.getEmail(),
                user.getRole(), token, "Login successful");
    }

    private boolean blank(String value) {
        return value == null || value.isBlank();
    }
}
