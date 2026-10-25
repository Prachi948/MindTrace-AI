
package com.mindtrace.mindtrace_backend.service;

import tools.jackson.databind.JsonNode;
import com.mindtrace.mindtrace_backend.dto.TutorRequest;
import com.mindtrace.mindtrace_backend.dto.TutorResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Service
public class TutorService {

    private final RestClient restClient;
    private final String apiKey;
    private final String model;

    public TutorService(
            RestClient.Builder builder,
            @Value("${app.ai.api-key:}") String apiKey,
            @Value("${app.ai.model:openai/gpt-oss-120b}") String model) {

        this.restClient = builder
                .baseUrl("https://api.groq.com/openai/v1")
                .build();

        this.apiKey = apiKey;
        this.model = model;
    }

    public TutorResponse answer(TutorRequest request) {

        if (request == null
                || request.getQuestion() == null
                || request.getQuestion().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "question is required"
            );
        }

        if (request.getQuestion().length() > 4000) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "question must be 4000 characters or fewer"
            );
        }

        if (apiKey == null || apiKey.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "AI tutor is not configured. Set the GROQ_API_KEY environment variable."
            );
        }

        String topic = request.getTopic() == null
                || request.getTopic().isBlank()
                ? "General learning"
                : request.getTopic().trim();

        String level = request.getLearnerLevel() == null
                || request.getLearnerLevel().isBlank()
                ? "beginner"
                : request.getLearnerLevel().trim();

        String systemPrompt = """
                You are MindTrace AI, a helpful, accurate, patient programming tutor.

                Your most important rule:
                Answer the learner's ACTUAL question directly. Do not use a generic
                response asking them to paste code unless their question genuinely
                requires code that they have not provided.

                Teaching instructions:
                1. If the learner asks a conceptual question, explain the concept directly.
                2. If they ask for an example, provide a complete, runnable example.
                3. If they ask for code, provide code and explain how it works.
                4. If they submit their own answer for review, evaluate that answer.
                5. If they provide code and ask for an explanation, explain their code.
                6. If essential information is missing, ask one specific clarification.
                7. Use clear headings, short paragraphs, numbered steps, and Markdown.
                8. Use fenced code blocks for code, specifying the programming language.
                9. Explain technical terms in beginner-friendly language.
                10. Finish with one relevant practice question when appropriate.
                11. Never claim that code was executed or tested unless it actually was.
                12. Do not repeat generic instructions instead of answering the question.

                Learner level: %s
                Learning topic: %s

                For programming examples, prefer simple and correct code.
                Adapt the answer to the learner's question and skill level.
                """.formatted(level, topic);

        try {
            JsonNode result = restClient.post()
                    .uri("/chat/completions")
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .body(Map.of(
                            "model", model,
                            "temperature", 0.3,
                            "max_tokens", 1400,
                            "messages", List.of(
                                    Map.of(
                                            "role", "system",
                                            "content", systemPrompt
                                    ),
                                    Map.of(
                                            "role", "user",
                                            "content", request.getQuestion().trim()
                                    )
                            )
                    ))
                    .retrieve()
                    .body(JsonNode.class);

            String answer = result == null
                    ? null
                    : result.path("choices")
                    .path(0)
                    .path("message")
                    .path("content")
                    .asText(null);

            if (answer == null || answer.isBlank()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_GATEWAY,
                        "AI provider returned an empty answer"
                );
            }

            return new TutorResponse(
                    topic,
                    answer,
                    "Try answering the practice question in the AI tutor response.",
                    "GROQ_LLM"
            );

        } catch (RestClientResponseException ex) {

            System.err.println("Groq status: " + ex.getStatusCode());
            System.err.println(
                    "Groq response: " + ex.getResponseBodyAsString()
            );

            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "AI provider request failed. Check IntelliJ Run console."
            );

        } catch (ResponseStatusException ex) {
            throw ex;

        } catch (Exception ex) {

            System.err.println("AI provider error: " + ex.getMessage());

            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Could not reach the AI provider. Check network access and configuration."
            );
        }
    }
}
