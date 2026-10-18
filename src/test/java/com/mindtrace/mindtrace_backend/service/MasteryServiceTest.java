package com.mindtrace.mindtrace_backend.service;

import com.mindtrace.mindtrace_backend.dto.MasteryRequest;
import com.mindtrace.mindtrace_backend.dto.MasteryResponse;
import com.mindtrace.mindtrace_backend.entity.AppUser;
import com.mindtrace.mindtrace_backend.entity.Concept;
import com.mindtrace.mindtrace_backend.entity.ConceptMastery;
import com.mindtrace.mindtrace_backend.repository.AppUserRepository;
import com.mindtrace.mindtrace_backend.repository.ConceptMasteryRepository;
import com.mindtrace.mindtrace_backend.repository.ConceptRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MasteryServiceTest {
    @Mock ConceptMasteryRepository masteryRepo;
    @Mock AppUserRepository userRepo;
    @Mock ConceptRepository conceptRepo;
    @Mock AppUser user;
    @Mock Concept concept;

    MasteryService service;

    @BeforeEach
    void setUp() {
        service = new MasteryService(masteryRepo, userRepo, conceptRepo);
        when(user.getId()).thenReturn(7L);
        when(concept.getId()).thenReturn(4L);
        when(concept.getName()).thenReturn("OOP");
        when(userRepo.findById(7L)).thenReturn(Optional.of(user));
        when(conceptRepo.findById(4L)).thenReturn(Optional.of(concept));
        when(masteryRepo.findByUserIdAndConceptId(7L, 4L)).thenReturn(Optional.empty());
        when(masteryRepo.save(any(ConceptMastery.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void upsertMapsScoreToNeedsPractice() {
        MasteryRequest request = new MasteryRequest();
        request.setMasteryScore(25);
        request.setNotes("Review needed");

        MasteryResponse response = service.upsert(7L, 4L, request);

        assertEquals(25, response.masteryScore());
        assertEquals("NEEDS_PRACTICE", response.masteryLevel());
        assertEquals("OOP", response.conceptName());
        assertEquals("Review needed", response.notes());
    }

    @Test
    void upsertMapsScoreToDeveloping() {
        MasteryRequest request = new MasteryRequest();
        request.setMasteryScore(55);

        assertEquals("DEVELOPING", service.upsert(7L, 4L, request).masteryLevel());
    }

    @Test
    void upsertMapsScoreToProficient() {
        MasteryRequest request = new MasteryRequest();
        request.setMasteryScore(75);

        assertEquals("PROFICIENT", service.upsert(7L, 4L, request).masteryLevel());
    }

    @Test
    void upsertMapsNinetyToMastered() {
        MasteryRequest request = new MasteryRequest();
        request.setMasteryScore(90);

        assertEquals("MASTERED", service.upsert(7L, 4L, request).masteryLevel());
    }

    @Test
    void upsertRejectsScoreOutsideRange() {
        MasteryRequest request = new MasteryRequest();
        request.setMasteryScore(101);

        assertThrows(ResponseStatusException.class, () -> service.upsert(7L, 4L, request));
        verify(masteryRepo, never()).save(any());
    }
}
