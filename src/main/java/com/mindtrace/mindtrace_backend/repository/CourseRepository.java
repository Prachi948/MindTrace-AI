package com.mindtrace.mindtrace_backend.repository;

import com.mindtrace.mindtrace_backend.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
}