package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.DisciplineAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Discipline;
import com.mycompany.myapp.repository.DisciplineRepository;
import jakarta.persistence.EntityManager;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link DisciplineResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class DisciplineResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/disciplines";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DisciplineRepository disciplineRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDisciplineMockMvc;

    private Discipline discipline;

    private Discipline insertedDiscipline;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Discipline createEntity() {
        return new Discipline().name(DEFAULT_NAME);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Discipline createUpdatedEntity() {
        return new Discipline().name(UPDATED_NAME);
    }

    @BeforeEach
    public void initTest() {
        discipline = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedDiscipline != null) {
            disciplineRepository.delete(insertedDiscipline);
            insertedDiscipline = null;
        }
    }

    @Test
    @Transactional
    void createDiscipline() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Discipline
        var returnedDiscipline = om.readValue(
            restDisciplineMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(discipline)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Discipline.class
        );

        // Validate the Discipline in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertDisciplineUpdatableFieldsEquals(returnedDiscipline, getPersistedDiscipline(returnedDiscipline));

        insertedDiscipline = returnedDiscipline;
    }

    @Test
    @Transactional
    void createDisciplineWithExistingId() throws Exception {
        // Create the Discipline with an existing ID
        discipline.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDisciplineMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(discipline)))
            .andExpect(status().isBadRequest());

        // Validate the Discipline in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        discipline.setName(null);

        // Create the Discipline, which fails.

        restDisciplineMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(discipline)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllDisciplines() throws Exception {
        // Initialize the database
        insertedDiscipline = disciplineRepository.saveAndFlush(discipline);

        // Get all the disciplineList
        restDisciplineMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(discipline.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }

    @Test
    @Transactional
    void getDiscipline() throws Exception {
        // Initialize the database
        insertedDiscipline = disciplineRepository.saveAndFlush(discipline);

        // Get the discipline
        restDisciplineMockMvc
            .perform(get(ENTITY_API_URL_ID, discipline.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(discipline.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME));
    }

    @Test
    @Transactional
    void getNonExistingDiscipline() throws Exception {
        // Get the discipline
        restDisciplineMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingDiscipline() throws Exception {
        // Initialize the database
        insertedDiscipline = disciplineRepository.saveAndFlush(discipline);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the discipline
        Discipline updatedDiscipline = disciplineRepository.findById(discipline.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedDiscipline are not directly saved in db
        em.detach(updatedDiscipline);
        updatedDiscipline.name(UPDATED_NAME);

        restDisciplineMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedDiscipline.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedDiscipline))
            )
            .andExpect(status().isOk());

        // Validate the Discipline in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedDisciplineToMatchAllProperties(updatedDiscipline);
    }

    @Test
    @Transactional
    void putNonExistingDiscipline() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        discipline.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDisciplineMockMvc
            .perform(
                put(ENTITY_API_URL_ID, discipline.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(discipline))
            )
            .andExpect(status().isBadRequest());

        // Validate the Discipline in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchDiscipline() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        discipline.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDisciplineMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(discipline))
            )
            .andExpect(status().isBadRequest());

        // Validate the Discipline in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDiscipline() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        discipline.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDisciplineMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(discipline)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Discipline in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateDisciplineWithPatch() throws Exception {
        // Initialize the database
        insertedDiscipline = disciplineRepository.saveAndFlush(discipline);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the discipline using partial update
        Discipline partialUpdatedDiscipline = new Discipline();
        partialUpdatedDiscipline.setId(discipline.getId());

        partialUpdatedDiscipline.name(UPDATED_NAME);

        restDisciplineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDiscipline.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDiscipline))
            )
            .andExpect(status().isOk());

        // Validate the Discipline in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDisciplineUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedDiscipline, discipline),
            getPersistedDiscipline(discipline)
        );
    }

    @Test
    @Transactional
    void fullUpdateDisciplineWithPatch() throws Exception {
        // Initialize the database
        insertedDiscipline = disciplineRepository.saveAndFlush(discipline);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the discipline using partial update
        Discipline partialUpdatedDiscipline = new Discipline();
        partialUpdatedDiscipline.setId(discipline.getId());

        partialUpdatedDiscipline.name(UPDATED_NAME);

        restDisciplineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDiscipline.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDiscipline))
            )
            .andExpect(status().isOk());

        // Validate the Discipline in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDisciplineUpdatableFieldsEquals(partialUpdatedDiscipline, getPersistedDiscipline(partialUpdatedDiscipline));
    }

    @Test
    @Transactional
    void patchNonExistingDiscipline() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        discipline.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDisciplineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, discipline.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(discipline))
            )
            .andExpect(status().isBadRequest());

        // Validate the Discipline in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDiscipline() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        discipline.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDisciplineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(discipline))
            )
            .andExpect(status().isBadRequest());

        // Validate the Discipline in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDiscipline() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        discipline.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDisciplineMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(discipline)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Discipline in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteDiscipline() throws Exception {
        // Initialize the database
        insertedDiscipline = disciplineRepository.saveAndFlush(discipline);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the discipline
        restDisciplineMockMvc
            .perform(delete(ENTITY_API_URL_ID, discipline.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return disciplineRepository.count();
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

    protected Discipline getPersistedDiscipline(Discipline discipline) {
        return disciplineRepository.findById(discipline.getId()).orElseThrow();
    }

    protected void assertPersistedDisciplineToMatchAllProperties(Discipline expectedDiscipline) {
        assertDisciplineAllPropertiesEquals(expectedDiscipline, getPersistedDiscipline(expectedDiscipline));
    }

    protected void assertPersistedDisciplineToMatchUpdatableProperties(Discipline expectedDiscipline) {
        assertDisciplineAllUpdatablePropertiesEquals(expectedDiscipline, getPersistedDiscipline(expectedDiscipline));
    }
}
