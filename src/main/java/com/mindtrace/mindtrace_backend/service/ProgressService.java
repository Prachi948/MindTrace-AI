package com.mindtrace.mindtrace_backend.service;

import com.mindtrace.mindtrace_backend.dto.*;
import com.mindtrace.mindtrace_backend.entity.*;
import com.mindtrace.mindtrace_backend.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProgressService {
    private final LessonProgressRepository progressRepo;
    private final AppUserRepository userRepo;
    private final LessonRepository lessonRepo;

    public ProgressService(LessonProgressRepository progressRepo, AppUserRepository userRepo,
                           LessonRepository lessonRepo) {
        this.progressRepo = progressRepo; this.userRepo = userRepo; this.lessonRepo = lessonRepo;
    }

    @Transactional
    public ProgressResponse update(Long userId, Long lessonId, ProgressRequest req) {
        if (req == null || req.getProgressPercentage() == null ||
            req.getProgressPercentage() < 0 || req.getProgressPercentage() > 100)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "progressPercentage must be from 0 to 100");
        AppUser user = userRepo.findById(userId).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Lesson lesson = lessonRepo.findById(lessonId).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Lesson not found"));
        LessonProgress p = progressRepo.findByUserIdAndLessonId(userId, lessonId).orElseGet(LessonProgress::new);
        if (p.getUser() == null) { p.setUser(user); p.setLesson(lesson); p.setStartedAt(LocalDateTime.now()); }
        int value = req.getProgressPercentage();
        p.setProgressPercentage(value);
        p.setCompleted(value == 100);
        p.setCompletedAt(value == 100 ? (p.getCompletedAt() == null ? LocalDateTime.now() : p.getCompletedAt()) : null);
        return toResponse(progressRepo.save(p));
    }

    @Transactional(readOnly = true)
    public List<ProgressResponse> list(Long userId) {
        if (!userRepo.existsById(userId)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        return progressRepo.findByUserIdOrderByStartedAtDesc(userId).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ProgressResponse get(Long userId, Long lessonId) {
        if (!userRepo.existsById(userId)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        return progressRepo.findByUserIdAndLessonId(userId, lessonId).map(this::toResponse)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Progress not found"));
    }

    @Transactional(readOnly = true)
    public ProgressSummaryResponse summary(Long userId) {
        if (!userRepo.existsById(userId)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        long started = progressRepo.findByUserIdOrderByStartedAtDesc(userId).size();
        long complete = progressRepo.countByUserIdAndCompletedTrue(userId);
        double pct = started == 0 ? 0 : Math.round(complete * 10000.0 / started) / 100.0;
        return new ProgressSummaryResponse(userId, started, complete, pct);
    }

    private ProgressResponse toResponse(LessonProgress p) {
        return new ProgressResponse(p.getId(), p.getUser().getId(), p.getLesson().getId(),
            p.getLesson().getTitle(), p.getProgressPercentage(), p.getCompleted(), p.getStartedAt(), p.getCompletedAt());
    }
}
