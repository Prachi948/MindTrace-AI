package com.mindtrace.mindtrace_backend.controller;
import com.mindtrace.mindtrace_backend.dto.*;
import com.mindtrace.mindtrace_backend.service.TutorService;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/tutor")
public class TutorController {
 private final TutorService service;
 public TutorController(TutorService service){this.service=service;}
 @PostMapping("/ask") public TutorResponse ask(@RequestBody TutorRequest request){return service.answer(request);}
}
