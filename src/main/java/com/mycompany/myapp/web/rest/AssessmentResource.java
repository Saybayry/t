package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Assessment;
import com.mycompany.myapp.repository.AssessmentRepository;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.Assessment}.
 */
@RestController
@RequestMapping("/api/assessments")
@Transactional
public class AssessmentResource {

    private static final Logger LOG = LoggerFactory.getLogger(AssessmentResource.class);

    private static final String ENTITY_NAME = "assessment";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AssessmentRepository assessmentRepository;

    public AssessmentResource(AssessmentRepository assessmentRepository) {
        this.assessmentRepository = assessmentRepository;
    }

    /**
     * {@code POST  /assessments} : Create a new assessment.
     *
     * @param assessment the assessment to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new assessment, or with status {@code 400 (Bad Request)} if the assessment has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Assessment> createAssessment(@Valid @RequestBody Assessment assessment) throws URISyntaxException {
        LOG.debug("REST request to save Assessment : {}", assessment);
        if (assessment.getId() != null) {
            throw new BadRequestAlertException("A new assessment cannot already have an ID", ENTITY_NAME, "idexists");
        }
        assessment = assessmentRepository.save(assessment);
        return ResponseEntity.created(new URI("/api/assessments/" + assessment.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, assessment.getId().toString()))
            .body(assessment);
    }

    /**
     * {@code PUT  /assessments/:id} : Updates an existing assessment.
     *
     * @param id the id of the assessment to save.
     * @param assessment the assessment to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated assessment,
     * or with status {@code 400 (Bad Request)} if the assessment is not valid,
     * or with status {@code 500 (Internal Server Error)} if the assessment couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Assessment> updateAssessment(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Assessment assessment
    ) throws URISyntaxException {
        LOG.debug("REST request to update Assessment : {}, {}", id, assessment);
        if (assessment.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, assessment.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!assessmentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        assessment = assessmentRepository.save(assessment);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, assessment.getId().toString()))
            .body(assessment);
    }

    /**
     * {@code PATCH  /assessments/:id} : Partial updates given fields of an existing assessment, field will ignore if it is null
     *
     * @param id the id of the assessment to save.
     * @param assessment the assessment to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated assessment,
     * or with status {@code 400 (Bad Request)} if the assessment is not valid,
     * or with status {@code 404 (Not Found)} if the assessment is not found,
     * or with status {@code 500 (Internal Server Error)} if the assessment couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Assessment> partialUpdateAssessment(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Assessment assessment
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Assessment partially : {}, {}", id, assessment);
        if (assessment.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, assessment.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!assessmentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Assessment> result = assessmentRepository
            .findById(assessment.getId())
            .map(existingAssessment -> {
                if (assessment.getIsPresent() != null) {
                    existingAssessment.setIsPresent(assessment.getIsPresent());
                }
                if (assessment.getAssessment() != null) {
                    existingAssessment.setAssessment(assessment.getAssessment());
                }

                return existingAssessment;
            })
            .map(assessmentRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, assessment.getId().toString())
        );
    }

    /**
     * {@code GET  /assessments} : get all the assessments.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of assessments in body.
     */
    @GetMapping("")
    public List<Assessment> getAllAssessments(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all Assessments");
        if (eagerload) {
            return assessmentRepository.findAllWithEagerRelationships();
        } else {
            return assessmentRepository.findAll();
        }
    }

    /**
     * {@code GET  /assessments/:id} : get the "id" assessment.
     *
     * @param id the id of the assessment to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the assessment, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Assessment> getAssessment(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Assessment : {}", id);
        Optional<Assessment> assessment = assessmentRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(assessment);
    }

    /**
     * {@code DELETE  /assessments/:id} : delete the "id" assessment.
     *
     * @param id the id of the assessment to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAssessment(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Assessment : {}", id);
        assessmentRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
