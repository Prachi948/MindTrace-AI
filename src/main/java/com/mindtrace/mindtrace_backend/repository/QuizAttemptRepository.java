package com.mindtrace.mindtrace_backend.repository;

import com.mindtrace.mindtrace_backend.entity.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {
}
