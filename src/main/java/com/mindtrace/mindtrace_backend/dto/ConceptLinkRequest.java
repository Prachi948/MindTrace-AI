package com.mindtrace.mindtrace_backend.dto;
public class ConceptLinkRequest {
 private Long sourceConceptId; private Long targetConceptId; private String relationshipType;
 public ConceptLinkRequest(){}
 public Long getSourceConceptId(){return sourceConceptId;} public void setSourceConceptId(Long v){sourceConceptId=v;}
 public Long getTargetConceptId(){return targetConceptId;} public void setTargetConceptId(Long v){targetConceptId=v;}
 public String getRelationshipType(){return relationshipType;} public void setRelationshipType(String v){relationshipType=v;}
}
