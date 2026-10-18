package com.mindtrace.mindtrace_backend.service;

import com.mindtrace.mindtrace_backend.dto.*;
import com.mindtrace.mindtrace_backend.entity.*;
import com.mindtrace.mindtrace_backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuizService {
    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final QuizAttemptRepository attemptRepository;

    public QuizService(QuizRepository q, QuestionRepository r, QuizAttemptRepository a) {
        quizRepository = q;
        questionRepository = r;
        attemptRepository = a;
    }

    @Transactional
    public QuizResultResponse submitQuiz(Long quizId, SubmitQuizRequest request) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new IllegalArgumentException("Quiz not found: " + quizId));
        if (request == null || request.answers() == null) throw new IllegalArgumentException("Answers are required");
        List<Question> questions = questionRepository.findByQuizId(quizId);
        if (questions.isEmpty()) throw new IllegalStateException("Quiz has no questions");
        int score = 0;
        for (var entry : request.answers().entrySet()) {
            Question question = questions.stream().filter(q -> q.getId().equals(entry.getKey())).findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Question does not belong to this quiz"));
            if (entry.getValue() != null && entry.getValue().trim().equalsIgnoreCase(question.getCorrectAnswer()))
                score++;
        }
        int total = questions.size();
        double percentage = Math.round(score * 10000.0 / total) / 100.0;
        boolean passed = percentage >= quiz.getPassingScore();
        QuizAttempt attempt = new QuizAttempt();
        attempt.setQuiz(quiz);
        attempt.setScore(score);
        attempt.setTotalQuestions(total);
        attempt.setPercentage(percentage);
        attempt.setPassed(passed);
        QuizAttempt saved = attemptRepository.save(attempt);
        return new QuizResultResponse(saved.getId(), score, total, percentage, passed);
    }
}
