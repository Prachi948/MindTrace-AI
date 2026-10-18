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
public class GamificationService {
 private final StudentRewardRepository rewards; private final AppUserRepository users;
 public GamificationService(StudentRewardRepository rewards,AppUserRepository users){this.rewards=rewards;this.users=users;}
 @Transactional
 public RewardResponse award(Long userId,String code,String title,int points){
  if(code==null||code.isBlank()||title==null||title.isBlank()||points<0)
   throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Valid reward code, title and non-negative points are required");
  AppUser user=users.findById(userId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found"));
  if(rewards.existsByUserIdAndRewardCode(userId,code))throw new ResponseStatusException(HttpStatus.CONFLICT,"Reward already awarded");
  StudentReward r=new StudentReward();r.setUser(user);r.setRewardCode(code);r.setTitle(title);r.setPoints(points);
  return map(rewards.save(r));
 }
 @Transactional(readOnly=true)
 public List<RewardResponse> list(Long userId){
  if(!users.existsById(userId))throw new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found");
  return rewards.findByUserIdOrderByAwardedAtDesc(userId).stream().map(this::map).toList();
 }
 @Transactional(readOnly=true)
 public PointsSummaryResponse summary(Long userId){
  List<StudentReward> rs=rewards.findByUserIdOrderByAwardedAtDesc(userId);
  if(!users.existsById(userId))throw new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found");
  return new PointsSummaryResponse(userId,rs.stream().mapToLong(r->r.getPoints()).sum(),rs.size());
 }
 private RewardResponse map(StudentReward r){return new RewardResponse(r.getId(),r.getUser().getId(),r.getRewardCode(),r.getTitle(),r.getPoints(),r.getAwardedAt());}
}
