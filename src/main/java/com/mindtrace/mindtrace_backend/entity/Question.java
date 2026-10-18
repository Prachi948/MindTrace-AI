package com.mindtrace.mindtrace_backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "questions")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String questionText;
    @Column(nullable = false)
    private String optionA;
    @Column(nullable = false)
    private String optionB;
    @Column(nullable = false)
    private String optionC;
    @Column(nullable = false)
    private String optionD;
    @Column(nullable = false)
    private String correctAnswer;
    @Column(columnDefinition = "TEXT")
    private String explanation;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    public Question() {
    }

    public Long getId() {
        return id;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String v) {
        questionText = v;
    }

    public String getOptionA() {
        return optionA;
    }

    public void setOptionA(String v) {
        optionA = v;
    }

    public String getOptionB() {
        return optionB;
    }

    public void setOptionB(String v) {
        optionB = v;
    }

    public String getOptionC() {
        return optionC;
    }

    public void setOptionC(String v) {
        optionC = v;
    }

    public String getOptionD() {
        return optionD;
    }

    public void setOptionD(String v) {
        optionD = v;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String v) {
        correctAnswer = v;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String v) {
        explanation = v;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz v) {
        quiz = v;
    }
}
