package com.mindtrace.mindtrace_backend.service;

import com.mindtrace.mindtrace_backend.dto.QuizResultResponse;
import com.mindtrace.mindtrace_backend.dto.SubmitQuizRequest;
import com.mindtrace.mindtrace_backend.entity.Question;
import com.mindtrace.mindtrace_backend.entity.Quiz;
import com.mindtrace.mindtrace_backend.entity.QuizAttempt;
import com.mindtrace.mindtrace_backend.repository.QuestionRepository;
import com.mindtrace.mindtrace_backend.repository.QuizAttemptRepository;
import com.mindtrace.mindtrace_backend.repository.QuizRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class QuizServiceTest {
    @Mock QuizRepository quizRepository;
    @Mock QuestionRepository questionRepository;
    @Mock QuizAttemptRepository attemptRepository;

    QuizService service;
    Quiz quiz;
    Question q1;
    Question q2;

    @BeforeEach
    void setUp() {
        service = new QuizService(quizRepository, questionRepository, attemptRepository);
        quiz = mock(Quiz.class);
        when(quiz.getPassingScore()).thenReturn(60);
        q1 = mock(Question.class);
        when(q1.getId()).thenReturn(11L);
        when(q1.getCorrectAnswer()).thenReturn("B");
        q2 = mock(Question.class);
        when(q2.getId()).thenReturn(12L);
        when(q2.getCorrectAnswer()).thenReturn("D");
        when(quizRepository.findById(5L)).thenReturn(Optional.of(quiz));
        when(questionRepository.findByQuizId(5L)).thenReturn(List.of(q1, q2));
        when(attemptRepository.save(any(QuizAttempt.class))).thenAnswer(inv -> {
            QuizAttempt attempt = inv.getArgument(0);
            QuizAttempt saved = mock(QuizAttempt.class);
            when(saved.getId()).thenReturn(99L);
            return saved;
        });
    }

    @Test
    void submitQuizCalculatesScoreAndPassesAtThreshold() {
        QuizResultResponse result = service.submitQuiz(5L,
                new SubmitQuizRequest(Map.of(11L, "b", 12L, "D")));

        assertEquals(99L, result.attemptId());
        assertEquals(2, result.score());
        assertEquals(2, result.totalQuestions());
        assertEquals(100.0, result.percentage());
        assertTrue(result.passed());
        verify(attemptRepository).save(any(QuizAttempt.class));
    }

    @Test
    void submitQuizCountsOnlyCorrectAnswersAndFailsBelowThreshold() {
        QuizResultResponse result = service.submitQuiz(5L,
                new SubmitQuizRequest(Map.of(11L, "A", 12L, "D")));

        assertEquals(1, result.score());
        assertEquals(50.0, result.percentage());
        assertFalse(result.passed());
    }

    @Test
    void submitQuizRejectsQuestionFromAnotherQuiz() {
        assertThrows(IllegalArgumentException.class,
                () -> service.submitQuiz(5L, new SubmitQuizRequest(Map.of(999L, "A"))));
        verify(attemptRepository, never()).save(any());
    }

    @Test
    void submitQuizRejectsMissingQuiz() {
        when(quizRepository.findById(404L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> service.submitQuiz(404L, new SubmitQuizRequest(Map.of(11L, "B"))));
        verify(attemptRepository, never()).save(any());
    }

    @Test
    void submitQuizRejectsEmptyQuestionSet() {
        when(questionRepository.findByQuizId(5L)).thenReturn(List.of());
        assertThrows(IllegalStateException.class,
                () -> service.submitQuiz(5L, new SubmitQuizRequest(Map.of())));
        verify(attemptRepository, never()).save(any());
    }
}
