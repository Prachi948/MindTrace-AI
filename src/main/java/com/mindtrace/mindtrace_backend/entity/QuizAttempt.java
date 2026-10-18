package com.mindtrace.mindtrace_backend.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "quiz_attempts")
public class QuizAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Integer score;
    @Column(nullable = false)
    private Integer totalQuestions;
    @Column(nullable = false)
    private Double percentage;
    @Column(nullable = false)
    private Boolean passed;
    @Column(nullable = false)
    private LocalDateTime submittedAt;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    public QuizAttempt() {
    }

    @PrePersist
    public void onCreate() {
        if (submittedAt == null) submittedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer v) {
        score = v;
    }

    public Integer getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(Integer v) {
        totalQuestions = v;
    }

    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double v) {
        percentage = v;
    }

    public Boolean getPassed() {
        return passed;
    }

    public void setPassed(Boolean v) {
        passed = v;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz v) {
        quiz = v;
    }
}
