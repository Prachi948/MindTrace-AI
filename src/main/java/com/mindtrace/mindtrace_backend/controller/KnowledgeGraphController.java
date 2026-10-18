package com.mindtrace.mindtrace_backend.controller;
import com.mindtrace.mindtrace_backend.dto.*;
import com.mindtrace.mindtrace_backend.service.KnowledgeGraphService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/knowledge-graph")
public class KnowledgeGraphController {
 private final KnowledgeGraphService service;
 public KnowledgeGraphController(KnowledgeGraphService service){this.service=service;}
 @PostMapping("/links") public ConceptLinkResponse create(@RequestBody ConceptLinkRequest r){return service.create(r);}
 @GetMapping("/concepts/{conceptId}/links") public List<ConceptLinkResponse> links(@PathVariable Long conceptId){return service.forConcept(conceptId);}
}
