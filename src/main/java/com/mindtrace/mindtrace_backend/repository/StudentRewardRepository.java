package com.mindtrace.mindtrace_backend.repository;
import com.mindtrace.mindtrace_backend.entity.StudentReward;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface StudentRewardRepository extends JpaRepository<StudentReward,Long>{
 List<StudentReward> findByUserIdOrderByAwardedAtDesc(Long userId);
 boolean existsByUserIdAndRewardCode(Long userId,String rewardCode);
}
