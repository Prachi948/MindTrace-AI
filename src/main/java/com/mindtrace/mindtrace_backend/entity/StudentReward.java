package com.mindtrace.mindtrace_backend.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity @Table(name="student_rewards",uniqueConstraints=@UniqueConstraint(name="uk_reward_user_code",columnNames={"user_id","reward_code"}))
public class StudentReward {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="user_id",nullable=false) private AppUser user;
 @Column(name="reward_code",nullable=false,length=80) private String rewardCode;
 @Column(nullable=false,length=120) private String title;
 @Column(nullable=false) private Integer points;
 @Column(nullable=false) private LocalDateTime awardedAt=LocalDateTime.now();
 public Long getId(){return id;} public AppUser getUser(){return user;} public void setUser(AppUser v){user=v;}
 public String getRewardCode(){return rewardCode;} public void setRewardCode(String v){rewardCode=v;}
 public String getTitle(){return title;} public void setTitle(String v){title=v;}
 public Integer getPoints(){return points;} public void setPoints(Integer v){points=v;}
 public LocalDateTime getAwardedAt(){return awardedAt;} public void setAwardedAt(LocalDateTime v){awardedAt=v;}
}
