package com.mindtrace.mindtrace_backend.dto;
public class TutorRequest {
 private String question; private String topic; private String learnerLevel;
 public TutorRequest(){}
 public String getQuestion(){return question;} public void setQuestion(String v){question=v;}
 public String getTopic(){return topic;} public void setTopic(String v){topic=v;}
 public String getLearnerLevel(){return learnerLevel;} public void setLearnerLevel(String v){learnerLevel=v;}
}
