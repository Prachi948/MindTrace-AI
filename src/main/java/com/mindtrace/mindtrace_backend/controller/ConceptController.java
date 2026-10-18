package com.mindtrace.mindtrace_backend.controller;

import com.mindtrace.mindtrace_backend.entity.Concept;
import com.mindtrace.mindtrace_backend.repository.ConceptRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/concepts")
@CrossOrigin(origins = "http://localhost:5173")
public class ConceptController {

    private final ConceptRepository repository;

    public ConceptController(ConceptRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Concept> getAllConcepts() {
        return repository.findAll();
    }

    @PostMapping
    public Concept addConcept(@RequestBody Concept concept) {
        return repository.save(concept);
    }

    @PutMapping("/{id}")
    public Concept updateConcept(
            @PathVariable Long id,
            @RequestBody Concept updatedConcept) {

        Concept existingConcept = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Concept not found: " + id));

        existingConcept.setName(updatedConcept.getName());

        return repository.save(existingConcept);
    }

    @DeleteMapping("/{id}")
    public void deleteConcept(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Concept not found: " + id);
        }

        repository.deleteById(id);
    }
}