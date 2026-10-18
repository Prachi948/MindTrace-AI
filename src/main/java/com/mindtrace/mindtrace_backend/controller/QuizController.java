package com.mindtrace.mindtrace_backend.controller;

import com.mindtrace.mindtrace_backend.dto.*;
import com.mindtrace.mindtrace_backend.entity.*;
import com.mindtrace.mindtrace_backend.repository.*;
import com.mindtrace.mindtrace_backend.service.QuizService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/quizzes")
@CrossOrigin(origins = "http://localhost:5173")
public class QuizController {
    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final LessonRepository lessonRepository;
    private final QuizService quizService;

    public QuizController(QuizRepository q, QuestionRepository r, LessonRepository l, QuizService s) {
        quizRepository = q;
        questionRepository = r;
        lessonRepository = l;
        quizService = s;
    }

    @PostMapping("/lesson/{lessonId}")
    public QuizResponse createQuiz(@PathVariable Long lessonId, @RequestBody QuizRequest request) {
        Lesson lesson = lessonRepository.findById(lessonId).orElseThrow(() -> new IllegalArgumentException("Lesson not found: " + lessonId));
        if (request.title() == null || request.title().isBlank())
            throw new IllegalArgumentException("Quiz title is required");
        int passing = request.passingScore() == null ? 60 : request.passingScore();
        if (passing < 0 || passing > 100) throw new IllegalArgumentException("passingScore must be 0-100");
        Quiz quiz = new Quiz();
        quiz.setTitle(request.title().trim());
        quiz.setDescription(request.description());
        quiz.setPassingScore(passing);
        quiz.setLesson(lesson);
        return response(quizRepository.save(quiz));
    }

    @GetMapping("/lesson/{lessonId}")
    public List<QuizResponse> getQuizzes(@PathVariable Long lessonId) {
        if (!lessonRepository.existsById(lessonId)) throw new IllegalArgumentException("Lesson not found: " + lessonId);
        return quizRepository.findByLessonId(lessonId).stream().map(this::response).toList();
    }

    @GetMapping("/{quizId}/questions")
    public List<QuestionResponse> getQuestions(@PathVariable Long quizId) {
        if (!quizRepository.existsById(quizId)) throw new IllegalArgumentException("Quiz not found: " + quizId);
        return questionRepository.findByQuizId(quizId).stream().map(q -> new QuestionResponse(q.getId(), q.getQuestionText(), q.getOptionA(), q.getOptionB(), q.getOptionC(), q.getOptionD())).toList();
    }

    @PostMapping("/{quizId}/questions")
    public QuestionResponse addQuestion(@PathVariable Long quizId, @RequestBody QuestionRequest r) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new IllegalArgumentException("Quiz not found: " + quizId));
        if (blank(r.questionText()) || blank(r.optionA()) || blank(r.optionB()) || blank(r.optionC()) || blank(r.optionD()))
            throw new IllegalArgumentException("Question text and all options are required");
        String answer = r.correctAnswer() == null ? "" : r.correctAnswer().trim().toUpperCase(Locale.ROOT);
        if (!List.of("A", "B", "C", "D").contains(answer))
            throw new IllegalArgumentException("correctAnswer must be A, B, C or D");
        Question q = new Question();
        q.setQuestionText(r.questionText().trim());
        q.setOptionA(r.optionA().trim());
        q.setOptionB(r.optionB().trim());
        q.setOptionC(r.optionC().trim());
        q.setOptionD(r.optionD().trim());
        q.setCorrectAnswer(answer);
        q.setExplanation(r.explanation());
        q.setQuiz(quiz);
        Question saved = questionRepository.save(q);
        return new QuestionResponse(saved.getId(), saved.getQuestionText(), saved.getOptionA(), saved.getOptionB(), saved.getOptionC(), saved.getOptionD());
    }

    @PostMapping("/{quizId}/submit")
    public QuizResultResponse submit(@PathVariable Long quizId, @RequestBody SubmitQuizRequest request) {
        return quizService.submitQuiz(quizId, request);
    }

    private QuizResponse response(Quiz q) {
        return new QuizResponse(q.getId(), q.getTitle(), q.getDescription(), q.getPassingScore(), q.getLesson().getId());
    }

    private boolean blank(String v) {
        return v == null || v.isBlank();
    }
}
