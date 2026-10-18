package com.mindtrace.mindtrace_backend.controller;

import com.mindtrace.mindtrace_backend.dto.LessonRequest;
import com.mindtrace.mindtrace_backend.dto.LessonResponse;
import com.mindtrace.mindtrace_backend.entity.Lesson;
import com.mindtrace.mindtrace_backend.entity.Module;
import com.mindtrace.mindtrace_backend.repository.LessonRepository;
import com.mindtrace.mindtrace_backend.repository.ModuleRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lessons")
@CrossOrigin(origins = "http://localhost:5173")
public class LessonController {

    private final LessonRepository lessonRepository;
    private final ModuleRepository moduleRepository;

    public LessonController(
            LessonRepository lessonRepository,
            ModuleRepository moduleRepository) {
        this.lessonRepository = lessonRepository;
        this.moduleRepository = moduleRepository;
    }

    // Get lessons belonging to a module
    @GetMapping("/module/{moduleId}")
    public List<LessonResponse> getLessonsByModule(
            @PathVariable Long moduleId) {

        if (!moduleRepository.existsById(moduleId)) {
            throw new RuntimeException("Module not found: " + moduleId);
        }

        return lessonRepository
                .findByModuleIdOrderBySequenceNumberAsc(moduleId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Create a lesson inside a module
    @PostMapping("/module/{moduleId}")
    public LessonResponse createLesson(
            @PathVariable Long moduleId,
            @RequestBody LessonRequest request) {

        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() ->
                        new RuntimeException("Module not found: " + moduleId));

        Lesson lesson = new Lesson();
        lesson.setTitle(request.title());
        lesson.setContent(request.content());
        lesson.setSequenceNumber(request.sequenceNumber());
        lesson.setModule(module);

        Lesson savedLesson = lessonRepository.save(lesson);

        return toResponse(savedLesson);
    }

    // Update a lesson
    @PutMapping("/{lessonId}")
    public LessonResponse updateLesson(
            @PathVariable Long lessonId,
            @RequestBody LessonRequest request) {

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() ->
                        new RuntimeException("Lesson not found: " + lessonId));

        lesson.setTitle(request.title());
        lesson.setContent(request.content());
        lesson.setSequenceNumber(request.sequenceNumber());

        return toResponse(lessonRepository.save(lesson));
    }

    // Delete a lesson
    @DeleteMapping("/{lessonId}")
    public void deleteLesson(@PathVariable Long lessonId) {

        if (!lessonRepository.existsById(lessonId)) {
            throw new RuntimeException("Lesson not found: " + lessonId);
        }

        lessonRepository.deleteById(lessonId);
    }

    private LessonResponse toResponse(Lesson lesson) {
        return new LessonResponse(
                lesson.getId(),
                lesson.getTitle(),
                lesson.getContent(),
                lesson.getSequenceNumber(),
                lesson.getModule().getId()
        );
    }
}