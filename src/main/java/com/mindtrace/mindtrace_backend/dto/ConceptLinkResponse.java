package com.mindtrace.mindtrace_backend.dto;
public record ConceptLinkResponse(Long id,Long sourceConceptId,String sourceConceptName,
 Long targetConceptId,String targetConceptName,String relationshipType){}
