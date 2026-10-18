package com.mindtrace.mindtrace_backend.dto;
import java.time.LocalDateTime;
public record MasteryResponse(Long id,Long userId,Long conceptId,String conceptName,Integer masteryScore,
 String masteryLevel,String notes,LocalDateTime updatedAt){}
