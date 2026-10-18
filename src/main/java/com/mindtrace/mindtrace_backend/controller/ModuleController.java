package com.mindtrace.mindtrace_backend.controller;

import com.mindtrace.mindtrace_backend.entity.Course;
import com.mindtrace.mindtrace_backend.entity.Module;
import com.mindtrace.mindtrace_backend.repository.CourseRepository;
import com.mindtrace.mindtrace_backend.repository.ModuleRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/modules")
@CrossOrigin(origins = "http://localhost:5173")
public class ModuleController {

    private final ModuleRepository moduleRepository;
    private final CourseRepository courseRepository;

    public ModuleController(
            ModuleRepository moduleRepository,
            CourseRepository courseRepository) {
        this.moduleRepository = moduleRepository;
        this.courseRepository = courseRepository;
    }

    // Get all modules for a specific course
    @GetMapping("/course/{courseId}")
    public List<Module> getModulesByCourse(@PathVariable Long courseId) {
        return moduleRepository.findByCourseId(courseId);
    }

    // Add a module to a course
    @PostMapping("/course/{courseId}")
    public Module addModule(
            @PathVariable Long courseId,
            @RequestBody Module module) {

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        module.setCourse(course);
        return moduleRepository.save(module);
    }
}