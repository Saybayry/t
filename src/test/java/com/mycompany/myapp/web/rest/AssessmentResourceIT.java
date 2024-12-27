package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.AssessmentAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Assessment;
import com.mycompany.myapp.repository.AssessmentRepository;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link AssessmentResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class AssessmentResourceIT {

    private static final Long DEFAULT_IS_PRESENT = 1L;
    private static final Long UPDATED_IS_PRESENT = 2L;

    private static final Integer DEFAULT_ASSESSMENT = 1;
    private static final Integer UPDATED_ASSESSMENT = 2;

    private static final String ENTITY_API_URL = "/api/assessments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AssessmentRepository assessmentRepository;

    @Mock
    private AssessmentRepository assessmentRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAssessmentMockMvc;

    private Assessment assessment;

    private Assessment insertedAssessment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Assessment createEntity() {
        return new Assessment().isPresent(DEFAULT_IS_PRESENT).assessment(DEFAULT_ASSESSMENT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Assessment createUpdatedEntity() {
        return new Assessment().isPresent(UPDATED_IS_PRESENT).assessment(UPDATED_ASSESSMENT);
    }

    @BeforeEach
    public void initTest() {
        assessment = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedAssessment != null) {
            assessmentRepository.delete(insertedAssessment);
            insertedAssessment = null;
        }
    }

    @Test
    @Transactional
    void createAssessment() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Assessment
        var returnedAssessment = om.readValue(
            restAssessmentMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(assessment)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Assessment.class
        );

        // Validate the Assessment in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertAssessmentUpdatableFieldsEquals(returnedAssessment, getPersistedAssessment(returnedAssessment));

        insertedAssessment = returnedAssessment;
    }

    @Test
    @Transactional
    void createAssessmentWithExistingId() throws Exception {
        // Create the Assessment with an existing ID
        assessment.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAssessmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(assessment)))
            .andExpect(status().isBadRequest());

        // Validate the Assessment in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkIsPresentIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        assessment.setIsPresent(null);

        // Create the Assessment, which fails.

        restAssessmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(assessment)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAssessmentIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        assessment.setAssessment(null);

        // Create the Assessment, which fails.

        restAssessmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(assessment)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAssessments() throws Exception {
        // Initialize the database
        insertedAssessment = assessmentRepository.saveAndFlush(assessment);

        // Get all the assessmentList
        restAssessmentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(assessment.getId().intValue())))
            .andExpect(jsonPath("$.[*].isPresent").value(hasItem(DEFAULT_IS_PRESENT.intValue())))
            .andExpect(jsonPath("$.[*].assessment").value(hasItem(DEFAULT_ASSESSMENT)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAssessmentsWithEagerRelationshipsIsEnabled() throws Exception {
        when(assessmentRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restAssessmentMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(assessmentRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAssessmentsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(assessmentRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restAssessmentMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(assessmentRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getAssessment() throws Exception {
        // Initialize the database
        insertedAssessment = assessmentRepository.saveAndFlush(assessment);

        // Get the assessment
        restAssessmentMockMvc
            .perform(get(ENTITY_API_URL_ID, assessment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(assessment.getId().intValue()))
            .andExpect(jsonPath("$.isPresent").value(DEFAULT_IS_PRESENT.intValue()))
            .andExpect(jsonPath("$.assessment").value(DEFAULT_ASSESSMENT));
    }

    @Test
    @Transactional
    void getNonExistingAssessment() throws Exception {
        // Get the assessment
        restAssessmentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAssessment() throws Exception {
        // Initialize the database
        insertedAssessment = assessmentRepository.saveAndFlush(assessment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the assessment
        Assessment updatedAssessment = assessmentRepository.findById(assessment.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAssessment are not directly saved in db
        em.detach(updatedAssessment);
        updatedAssessment.isPresent(UPDATED_IS_PRESENT).assessment(UPDATED_ASSESSMENT);

        restAssessmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedAssessment.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedAssessment))
            )
            .andExpect(status().isOk());

        // Validate the Assessment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAssessmentToMatchAllProperties(updatedAssessment);
    }

    @Test
    @Transactional
    void putNonExistingAssessment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assessment.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAssessmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, assessment.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(assessment))
            )
            .andExpect(status().isBadRequest());

        // Validate the Assessment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAssessment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assessment.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAssessmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(assessment))
            )
            .andExpect(status().isBadRequest());

        // Validate the Assessment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAssessment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assessment.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAssessmentMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(assessment)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Assessment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAssessmentWithPatch() throws Exception {
        // Initialize the database
        insertedAssessment = assessmentRepository.saveAndFlush(assessment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the assessment using partial update
        Assessment partialUpdatedAssessment = new Assessment();
        partialUpdatedAssessment.setId(assessment.getId());

        partialUpdatedAssessment.isPresent(UPDATED_IS_PRESENT);

        restAssessmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAssessment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAssessment))
            )
            .andExpect(status().isOk());

        // Validate the Assessment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAssessmentUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedAssessment, assessment),
            getPersistedAssessment(assessment)
        );
    }

    @Test
    @Transactional
    void fullUpdateAssessmentWithPatch() throws Exception {
        // Initialize the database
        insertedAssessment = assessmentRepository.saveAndFlush(assessment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the assessment using partial update
        Assessment partialUpdatedAssessment = new Assessment();
        partialUpdatedAssessment.setId(assessment.getId());

        partialUpdatedAssessment.isPresent(UPDATED_IS_PRESENT).assessment(UPDATED_ASSESSMENT);

        restAssessmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAssessment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAssessment))
            )
            .andExpect(status().isOk());

        // Validate the Assessment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAssessmentUpdatableFieldsEquals(partialUpdatedAssessment, getPersistedAssessment(partialUpdatedAssessment));
    }

    @Test
    @Transactional
    void patchNonExistingAssessment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assessment.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAssessmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, assessment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(assessment))
            )
            .andExpect(status().isBadRequest());

        // Validate the Assessment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAssessment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assessment.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAssessmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(assessment))
            )
            .andExpect(status().isBadRequest());

        // Validate the Assessment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAssessment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assessment.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAssessmentMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(assessment)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Assessment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAssessment() throws Exception {
        // Initialize the database
        insertedAssessment = assessmentRepository.saveAndFlush(assessment);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the assessment
        restAssessmentMockMvc
            .perform(delete(ENTITY_API_URL_ID, assessment.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return assessmentRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Assessment getPersistedAssessment(Assessment assessment) {
        return assessmentRepository.findById(assessment.getId()).orElseThrow();
    }

    protected void assertPersistedAssessmentToMatchAllProperties(Assessment expectedAssessment) {
        assertAssessmentAllPropertiesEquals(expectedAssessment, getPersistedAssessment(expectedAssessment));
    }

    protected void assertPersistedAssessmentToMatchUpdatableProperties(Assessment expectedAssessment) {
        assertAssessmentAllUpdatablePropertiesEquals(expectedAssessment, getPersistedAssessment(expectedAssessment));
    }
}
