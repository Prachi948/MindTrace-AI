package com.mindtrace.mindtrace_backend.repository;
import com.mindtrace.mindtrace_backend.entity.ConceptLink;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ConceptLinkRepository extends JpaRepository<ConceptLink,Long>{
 List<ConceptLink> findBySourceConceptIdOrTargetConceptId(Long sourceId,Long targetId);
}
