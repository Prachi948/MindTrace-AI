package com.mindtrace.mindtrace_backend.service;
import com.mindtrace.mindtrace_backend.dto.*;
import com.mindtrace.mindtrace_backend.entity.*;
import com.mindtrace.mindtrace_backend.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
@Service
public class KnowledgeGraphService {
 private final ConceptLinkRepository links; private final ConceptRepository concepts;
 public KnowledgeGraphService(ConceptLinkRepository links,ConceptRepository concepts){this.links=links;this.concepts=concepts;}
 @Transactional
 public ConceptLinkResponse create(ConceptLinkRequest r){
  if(r==null||r.getSourceConceptId()==null||r.getTargetConceptId()==null||
    r.getRelationshipType()==null||r.getRelationshipType().isBlank())
   throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"sourceConceptId, targetConceptId and relationshipType are required");
  if(r.getSourceConceptId().equals(r.getTargetConceptId()))
   throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"A concept cannot link to itself");
  Concept s=concepts.findById(r.getSourceConceptId()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Source concept not found"));
  Concept t=concepts.findById(r.getTargetConceptId()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Target concept not found"));
  ConceptLink l=new ConceptLink();l.setSourceConcept(s);l.setTargetConcept(t);l.setRelationshipType(r.getRelationshipType().trim());
  return map(links.save(l));
 }
 @Transactional(readOnly=true)
 public List<ConceptLinkResponse> forConcept(Long conceptId){
  if(!concepts.existsById(conceptId))throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Concept not found");
  return links.findBySourceConceptIdOrTargetConceptId(conceptId,conceptId).stream().map(this::map).toList();
 }
 private ConceptLinkResponse map(ConceptLink l){return new ConceptLinkResponse(l.getId(),
  l.getSourceConcept().getId(),l.getSourceConcept().getName(),l.getTargetConcept().getId(),
  l.getTargetConcept().getName(),l.getRelationshipType());}
}
