package com.mindtrace.mindtrace_backend.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="concept_mastery", uniqueConstraints=@UniqueConstraint(
 name="uk_mastery_user_concept", columnNames={"user_id","concept_id"}))
public class ConceptMastery {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="user_id", nullable=false) private AppUser user;
 @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="concept_id", nullable=false) private Concept concept;
 @Column(nullable=false) private Integer masteryScore=0;
 @Column(length=1000) private String notes;
 @Column(nullable=false) private LocalDateTime updatedAt=LocalDateTime.now();
 @PreUpdate void touch(){ updatedAt=LocalDateTime.now(); }
 public Long getId(){return id;} public AppUser getUser(){return user;} public void setUser(AppUser v){user=v;}
 public Concept getConcept(){return concept;} public void setConcept(Concept v){concept=v;}
 public Integer getMasteryScore(){return masteryScore;} public void setMasteryScore(Integer v){masteryScore=v;}
 public String getNotes(){return notes;} public void setNotes(String v){notes=v;}
 public LocalDateTime getUpdatedAt(){return updatedAt;} public void setUpdatedAt(LocalDateTime v){updatedAt=v;}
}
