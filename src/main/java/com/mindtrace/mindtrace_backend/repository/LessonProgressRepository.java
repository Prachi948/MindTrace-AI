package com.mindtrace.mindtrace_backend.repository;

import com.mindtrace.mindtrace_backend.entity.LessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface LessonProgressRepository extends JpaRepository<LessonProgress, Long> {
    Optional<LessonProgress> findByUserIdAndLessonId(Long userId, Long lessonId);
    List<LessonProgress> findByUserIdOrderByStartedAtDesc(Long userId);
    long countByUserIdAndCompletedTrue(Long userId);
}
