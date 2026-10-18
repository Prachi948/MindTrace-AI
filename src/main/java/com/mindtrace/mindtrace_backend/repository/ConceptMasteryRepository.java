package com.mindtrace.mindtrace_backend.repository;
import com.mindtrace.mindtrace_backend.entity.ConceptMastery;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface ConceptMasteryRepository extends JpaRepository<ConceptMastery,Long>{
 Optional<ConceptMastery> findByUserIdAndConceptId(Long userId,Long conceptId);
 List<ConceptMastery> findByUserIdOrderByMasteryScoreAsc(Long userId);
}
