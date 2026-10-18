package com.mindtrace.mindtrace_backend.repository;

import com.mindtrace.mindtrace_backend.entity.Concept;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConceptRepository extends JpaRepository<Concept, Long> {
}