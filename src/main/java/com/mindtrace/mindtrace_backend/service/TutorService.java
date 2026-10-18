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

    public TutorService(RestClient.Builder builder,
                        @Value("${app.ai.api-key:}") String apiKey,
                        @Value("${app.ai.model:llama-3.1-8b-instant}") String model) {
        this.restClient = builder.baseUrl("https://api.groq.com/openai/v1").build();
        this.apiKey = apiKey;
        this.model = model;
    }

    public TutorResponse answer(TutorRequest request) {
        if (request == null || request.getQuestion() == null || request.getQuestion().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "question is required");
        if (request.getQuestion().length() > 4000)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "question must be 4000 characters or fewer");
        if (apiKey == null || apiKey.isBlank())
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "AI tutor is not configured. Set the GROQ_API_KEY environment variable.");

        String topic = request.getTopic() == null || request.getTopic().isBlank()
                ? "General learning" : request.getTopic().trim();
        String level = request.getLearnerLevel() == null || request.getLearnerLevel().isBlank()
                ? "beginner" : request.getLearnerLevel().trim();

        String systemPrompt = "You are MindTrace AI, a patient educational tutor. "
                + "Teach safely and accurately. Explain in clear steps, use a small example, "
                + "and end with one short practice question. Adapt for learner level: " + level
                + ". Topic: " + topic + ". If uncertain, say so rather than inventing facts.";

        try {
            JsonNode result = restClient.post()
                    .uri("/chat/completions")
                    .header("Authorization", "Bearer " + apiKey)
                    .body(Map.of(
                            "model", model,
                            "temperature", 0.3,
                            "max_tokens", 900,
                            "messages", List.of(
                                    Map.of("role", "system", "content", systemPrompt),
                                    Map.of("role", "user", "content", request.getQuestion().trim())
                            )))
                    .retrieve()
                    .body(JsonNode.class);

            String answer = result == null ? null
                    : result.path("choices").path(0).path("message").path("content").asText(null);
            if (answer == null || answer.isBlank())
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "AI provider returned an empty answer");

            return new TutorResponse(topic, answer,
                    "Try explaining the idea in your own words, then answer the practice question in the response.",
                    "GROQ_LLM");
        } catch (RestClientResponseException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "AI provider request failed (HTTP " + ex.getStatusCode().value() + "). Check the API key and model.");
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "Could not reach the AI provider. Check network access and configuration.");
        }
    }
}
