package com.mindtrace.mindtrace_backend.repository;

import com.mindtrace.mindtrace_backend.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, Long> {

    List<Lesson> findByModuleIdOrderBySequenceNumberAsc(Long moduleId);
}