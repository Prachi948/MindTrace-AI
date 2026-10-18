package com.mindtrace.mindtrace_backend.service;
import com.mindtrace.mindtrace_backend.dto.*;
import com.mindtrace.mindtrace_backend.entity.*;
import com.mindtrace.mindtrace_backend.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MasteryService {
 private final ConceptMasteryRepository repo; private final AppUserRepository users; private final ConceptRepository concepts;
 public MasteryService(ConceptMasteryRepository repo,AppUserRepository users,ConceptRepository concepts){
  this.repo=repo;this.users=users;this.concepts=concepts;
 }
 @Transactional
 public MasteryResponse upsert(Long userId,Long conceptId,MasteryRequest req){
  if(req==null||req.getMasteryScore()==null||req.getMasteryScore()<0||req.getMasteryScore()>100)
   throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"masteryScore must be from 0 to 100");
  AppUser user=users.findById(userId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found"));
  Concept concept=concepts.findById(conceptId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Concept not found"));
  ConceptMastery m=repo.findByUserIdAndConceptId(userId,conceptId).orElseGet(ConceptMastery::new);
  if(m.getUser()==null){m.setUser(user);m.setConcept(concept);}
  m.setMasteryScore(req.getMasteryScore());m.setNotes(req.getNotes());m.setUpdatedAt(LocalDateTime.now());
  return map(repo.save(m));
 }
 @Transactional(readOnly=true)
 public List<MasteryResponse> list(Long userId){
  if(!users.existsById(userId))throw new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found");
  return repo.findByUserIdOrderByMasteryScoreAsc(userId).stream().map(this::map).toList();
 }
 private MasteryResponse map(ConceptMastery m){
  int s=m.getMasteryScore();String level=s<40?"NEEDS_PRACTICE":s<70?"DEVELOPING":s<90?"PROFICIENT":"MASTERED";
  return new MasteryResponse(m.getId(),m.getUser().getId(),m.getConcept().getId(),
   m.getConcept().getName(),s,level,m.getNotes(),m.getUpdatedAt());
 }
}
