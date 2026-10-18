package com.mindtrace.mindtrace_backend.entity;
import jakarta.persistence.*;
@Entity
@Table(name="concept_links", uniqueConstraints=@UniqueConstraint(name="uk_concept_link",
 columnNames={"source_concept_id","target_concept_id"}))
public class ConceptLink {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="source_concept_id",nullable=false) private Concept sourceConcept;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="target_concept_id",nullable=false) private Concept targetConcept;
 @Column(length=80,nullable=false) private String relationshipType;
 public Long getId(){return id;} public Concept getSourceConcept(){return sourceConcept;} public void setSourceConcept(Concept v){sourceConcept=v;}
 public Concept getTargetConcept(){return targetConcept;} public void setTargetConcept(Concept v){targetConcept=v;}
 public String getRelationshipType(){return relationshipType;} public void setRelationshipType(String v){relationshipType=v;}
}
