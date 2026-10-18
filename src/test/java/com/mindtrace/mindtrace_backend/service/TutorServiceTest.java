package com.mindtrace.mindtrace_backend.service;

import com.mindtrace.mindtrace_backend.dto.TutorRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TutorServiceTest {
    RestClient.Builder builder;
    RestClient restClient;

    @BeforeEach
    void setUp() {
        builder = mock(RestClient.Builder.class);
        restClient = mock(RestClient.class);
        when(builder.baseUrl(anyString())).thenReturn(builder);
        when(builder.build()).thenReturn(restClient);
    }

    @Test
    void answerRejectsBlankQuestionBeforeCallingProvider() {
        TutorService service = new TutorService(builder, "test-key", "test-model");
        TutorRequest request = new TutorRequest();
        request.setQuestion("   ");

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.answer(request));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void answerReturnsServiceUnavailableWhenApiKeyMissing() {
        TutorService service = new TutorService(builder, "", "test-model");
        TutorRequest request = new TutorRequest();
        request.setQuestion("Explain Java variables");

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.answer(request));

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, ex.getStatusCode());
    }

    @Test
    void answerRejectsQuestionOverLimit() {
        TutorService service = new TutorService(builder, "test-key", "test-model");
        TutorRequest request = new TutorRequest();
        request.setQuestion("x".repeat(4001));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.answer(request));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }
}
