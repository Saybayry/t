package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Assessment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Assessment entity.
 */
@Repository
public interface AssessmentRepository extends JpaRepository<Assessment, Long> {
    default Optional<Assessment> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Assessment> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Assessment> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select assessment from Assessment assessment left join fetch assessment.student left join fetch assessment.lesson",
        countQuery = "select count(assessment) from Assessment assessment"
    )
    Page<Assessment> findAllWithToOneRelationships(Pageable pageable);

    @Query("select assessment from Assessment assessment left join fetch assessment.student left join fetch assessment.lesson")
    List<Assessment> findAllWithToOneRelationships();

    @Query(
        "select assessment from Assessment assessment left join fetch assessment.student left join fetch assessment.lesson where assessment.id =:id"
    )
    Optional<Assessment> findOneWithToOneRelationships(@Param("id") Long id);
}
