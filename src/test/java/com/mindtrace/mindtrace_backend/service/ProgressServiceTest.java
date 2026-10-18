package com.mindtrace.mindtrace_backend.service;

import com.mindtrace.mindtrace_backend.dto.ProgressRequest;
import com.mindtrace.mindtrace_backend.dto.ProgressResponse;
import com.mindtrace.mindtrace_backend.entity.AppUser;
import com.mindtrace.mindtrace_backend.entity.Lesson;
import com.mindtrace.mindtrace_backend.entity.LessonProgress;
import com.mindtrace.mindtrace_backend.repository.AppUserRepository;
import com.mindtrace.mindtrace_backend.repository.LessonProgressRepository;
import com.mindtrace.mindtrace_backend.repository.LessonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ProgressServiceTest {
    @Mock LessonProgressRepository progressRepo;
    @Mock AppUserRepository userRepo;
    @Mock LessonRepository lessonRepo;
    @Mock AppUser user;
    @Mock Lesson lesson;

    ProgressService service;

    @BeforeEach
    void setUp() {
        service = new ProgressService(progressRepo, userRepo, lessonRepo);
        when(user.getId()).thenReturn(7L);
        when(lesson.getId()).thenReturn(3L);
        when(lesson.getTitle()).thenReturn("Java Variables");
        when(userRepo.findById(7L)).thenReturn(Optional.of(user));
        when(lessonRepo.findById(3L)).thenReturn(Optional.of(lesson));
        when(progressRepo.findByUserIdAndLessonId(7L, 3L)).thenReturn(Optional.empty());
        when(progressRepo.save(any(LessonProgress.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void updateStoresPartialProgress() {
        ProgressRequest request = new ProgressRequest();
        request.setProgressPercentage(60);

        ProgressResponse response = service.update(7L, 3L, request);

        assertEquals(7L, response.userId());
        assertEquals(3L, response.lessonId());
        assertEquals("Java Variables", response.lessonTitle());
        assertEquals(60, response.progressPercentage());
        assertFalse(response.completed());
        verify(progressRepo).save(any(LessonProgress.class));
    }

    @Test
    void updateMarksLessonCompletedAtOneHundredPercent() {
        ProgressRequest request = new ProgressRequest();
        request.setProgressPercentage(100);

        ProgressResponse response = service.update(7L, 3L, request);

        assertEquals(100, response.progressPercentage());
        assertTrue(response.completed());
        assertNotNull(response.completedAt());
    }

    @Test
    void updateRejectsProgressAboveOneHundred() {
        ProgressRequest request = new ProgressRequest();
        request.setProgressPercentage(101);

        assertThrows(ResponseStatusException.class, () -> service.update(7L, 3L, request));
        verify(progressRepo, never()).save(any());
    }

    @Test
    void updateRejectsUnknownUser() {
        when(userRepo.findById(77L)).thenReturn(Optional.empty());
        ProgressRequest request = new ProgressRequest();
        request.setProgressPercentage(20);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.update(77L, 3L, request));
        assertEquals(404, ex.getStatusCode().value());
    }
}
