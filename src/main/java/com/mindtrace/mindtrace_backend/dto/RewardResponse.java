package com.mindtrace.mindtrace_backend.dto;
import java.time.LocalDateTime;
public record RewardResponse(Long id,Long userId,String rewardCode,String title,Integer points,LocalDateTime awardedAt){}
