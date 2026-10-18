package com.mindtrace.mindtrace_backend.service;
import com.mindtrace.mindtrace_backend.dto.LearningAnalyticsResponse;
import com.mindtrace.mindtrace_backend.entity.ConceptMastery;
import com.mindtrace.mindtrace_backend.entity.LessonProgress;
import com.mindtrace.mindtrace_backend.entity.StudentReward;
import com.mindtrace.mindtrace_backend.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
@Service
public class LearningAnalyticsService {
 private final AppUserRepository users; private final LessonProgressRepository progress;
 private final ConceptMasteryRepository mastery; private final StudentRewardRepository rewards;
 public LearningAnalyticsService(AppUserRepository users,LessonProgressRepository progress,
  ConceptMasteryRepository mastery,StudentRewardRepository rewards){
  this.users=users;this.progress=progress;this.mastery=mastery;this.rewards=rewards;
 }
 @Transactional(readOnly=true)
 public LearningAnalyticsResponse get(Long userId){
  if(!users.existsById(userId))throw new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found");
  List<LessonProgress> ps=progress.findByUserIdOrderByStartedAtDesc(userId);
  long done=ps.stream().filter(p->Boolean.TRUE.equals(p.getCompleted())).count();
  double pct=ps.isEmpty()?0:Math.round(done*10000.0/ps.size())/100.0;
  List<ConceptMastery> ms=mastery.findByUserIdOrderByMasteryScoreAsc(userId);
  double avg=ms.isEmpty()?0:Math.round(ms.stream().mapToInt(ConceptMastery::getMasteryScore).average().orElse(0)*100.0)/100.0;
  long points=rewards.findByUserIdOrderByAwardedAtDesc(userId).stream().mapToLong(StudentReward::getPoints).sum();
  return new LearningAnalyticsResponse(userId,ps.size(),done,pct,ms.size(),avg,points);
 }
}
