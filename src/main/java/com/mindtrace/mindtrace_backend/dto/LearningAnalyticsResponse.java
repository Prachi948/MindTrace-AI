package com.mindtrace.mindtrace_backend.dto;
public record LearningAnalyticsResponse(Long userId,long lessonsStarted,long lessonsCompleted,
 double lessonCompletionPercentage,long conceptsTracked,double averageMasteryScore,long totalPoints){}
