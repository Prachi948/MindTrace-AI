package com.mindtrace.mindtrace_backend.service;

import com.mindtrace.mindtrace_backend.dto.AuthResponse;
import com.mindtrace.mindtrace_backend.dto.LoginRequest;
import com.mindtrace.mindtrace_backend.dto.SignupRequest;
import com.mindtrace.mindtrace_backend.entity.AppUser;
import com.mindtrace.mindtrace_backend.repository.AppUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock AppUserRepository userRepository;
    @Mock JwtService jwtService;

    BCryptPasswordEncoder encoder;
    AuthService service;

    @BeforeEach
    void setUp() {
        encoder = new BCryptPasswordEncoder();
        service = new AuthService(userRepository, encoder, jwtService);
    }

    @Test
    void signupHashesPasswordAndReturnsToken() {
        AppUser saved = mock(AppUser.class);
        when(saved.getId()).thenReturn(42L);
        when(saved.getUsername()).thenReturn("testuser");
        when(saved.getEmail()).thenReturn("test@example.com");
        when(saved.getRole()).thenReturn("STUDENT");
        when(userRepository.existsByUsernameIgnoreCase("testuser")).thenReturn(false);
        when(userRepository.existsByEmailIgnoreCase("test@example.com")).thenReturn(false);
        when(userRepository.save(any(AppUser.class))).thenAnswer(inv -> {
            AppUser input = inv.getArgument(0);
            assertNotEquals("Password123", input.getPasswordHash());
            assertTrue(encoder.matches("Password123", input.getPasswordHash()));
            return saved;
        });
        when(jwtService.generateToken(42L, "STUDENT")).thenReturn("test.jwt.token");

        AuthResponse response = service.signup(new SignupRequest(" testuser ", "TEST@example.com", "Password123"));

        assertEquals(42L, response.userId());
        assertEquals("testuser", response.username());
        assertEquals("test@example.com", response.email());
        assertEquals("test.jwt.token", response.accessToken());
        verify(userRepository).save(any(AppUser.class));
    }

    @Test
    void signupRejectsWeakPassword() {
        assertThrows(IllegalArgumentException.class,
                () -> service.signup(new SignupRequest("testuser", "test@example.com", "short")));
        verify(userRepository, never()).save(any());
    }

    @Test
    void signupRejectsDuplicateEmail() {
        when(userRepository.existsByUsernameIgnoreCase("testuser")).thenReturn(false);
        when(userRepository.existsByEmailIgnoreCase("test@example.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> service.signup(new SignupRequest("testuser", "test@example.com", "Password123")));
        verify(userRepository, never()).save(any());
    }

    @Test
    void loginReturnsTokenForCorrectPassword() {
        AppUser user = mock(AppUser.class);
        when(user.getId()).thenReturn(42L);
        when(user.getUsername()).thenReturn("testuser");
        when(user.getEmail()).thenReturn("test@example.com");
        when(user.getRole()).thenReturn("STUDENT");
        when(user.getPasswordHash()).thenReturn(encoder.encode("Password123"));
        when(userRepository.findByEmailIgnoreCase("test@example.com")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(42L, "STUDENT")).thenReturn("test.jwt.token");

        AuthResponse response = service.login(new LoginRequest("test@example.com", "Password123"));

        assertEquals("test.jwt.token", response.accessToken());
    }

    @Test
    void loginRejectsWrongPassword() {
        AppUser user = mock(AppUser.class);
        when(user.getPasswordHash()).thenReturn(encoder.encode("Different123"));
        when(userRepository.findByEmailIgnoreCase("test@example.com")).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class,
                () -> service.login(new LoginRequest("test@example.com", "WrongPass123")));
        verify(jwtService, never()).generateToken(any(), any());
    }
}
