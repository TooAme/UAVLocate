package com.chenhy.web.rest;

import static com.chenhy.domain.StaticsAsserts.*;
import static com.chenhy.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.chenhy.IntegrationTest;
import com.chenhy.domain.Statics;
import com.chenhy.repository.StaticsRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link StaticsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class StaticsResourceIT {

    private static final Instant DEFAULT_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Long DEFAULT_POS_X = 1L;
    private static final Long UPDATED_POS_X = 2L;

    private static final Long DEFAULT_POS_Y = 1L;
    private static final Long UPDATED_POS_Y = 2L;

    private static final Long DEFAULT_POS_Z = 1L;
    private static final Long UPDATED_POS_Z = 2L;

    private static final Long DEFAULT_WIND_SPEED = 1L;
    private static final Long UPDATED_WIND_SPEED = 2L;

    private static final String DEFAULT_WIND_DIRECTION = "AAAAAAAAAA";
    private static final String UPDATED_WIND_DIRECTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/statics";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private StaticsRepository staticsRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restStaticsMockMvc;

    private Statics statics;

    private Statics insertedStatics;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Statics createEntity() {
        return new Statics()
            .time(DEFAULT_TIME)
            .posX(DEFAULT_POS_X)
            .posY(DEFAULT_POS_Y)
            .posZ(DEFAULT_POS_Z)
            .windSpeed(DEFAULT_WIND_SPEED)
            .windDirection(DEFAULT_WIND_DIRECTION);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Statics createUpdatedEntity() {
        return new Statics()
            .time(UPDATED_TIME)
            .posX(UPDATED_POS_X)
            .posY(UPDATED_POS_Y)
            .posZ(UPDATED_POS_Z)
            .windSpeed(UPDATED_WIND_SPEED)
            .windDirection(UPDATED_WIND_DIRECTION);
    }

    @BeforeEach
    public void initTest() {
        statics = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedStatics != null) {
            staticsRepository.delete(insertedStatics);
            insertedStatics = null;
        }
    }

    @Test
    @Transactional
    void createStatics() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Statics
        var returnedStatics = om.readValue(
            restStaticsMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(statics)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Statics.class
        );

        // Validate the Statics in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertStaticsUpdatableFieldsEquals(returnedStatics, getPersistedStatics(returnedStatics));

        insertedStatics = returnedStatics;
    }

    @Test
    @Transactional
    void createStaticsWithExistingId() throws Exception {
        // Create the Statics with an existing ID
        statics.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restStaticsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(statics)))
            .andExpect(status().isBadRequest());

        // Validate the Statics in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllStatics() throws Exception {
        // Initialize the database
        insertedStatics = staticsRepository.saveAndFlush(statics);

        // Get all the staticsList
        restStaticsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(statics.getId().intValue())))
            .andExpect(jsonPath("$.[*].time").value(hasItem(DEFAULT_TIME.toString())))
            .andExpect(jsonPath("$.[*].posX").value(hasItem(DEFAULT_POS_X.intValue())))
            .andExpect(jsonPath("$.[*].posY").value(hasItem(DEFAULT_POS_Y.intValue())))
            .andExpect(jsonPath("$.[*].posZ").value(hasItem(DEFAULT_POS_Z.intValue())))
            .andExpect(jsonPath("$.[*].windSpeed").value(hasItem(DEFAULT_WIND_SPEED.intValue())))
            .andExpect(jsonPath("$.[*].windDirection").value(hasItem(DEFAULT_WIND_DIRECTION)));
    }

    @Test
    @Transactional
    void getStatics() throws Exception {
        // Initialize the database
        insertedStatics = staticsRepository.saveAndFlush(statics);

        // Get the statics
        restStaticsMockMvc
            .perform(get(ENTITY_API_URL_ID, statics.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(statics.getId().intValue()))
            .andExpect(jsonPath("$.time").value(DEFAULT_TIME.toString()))
            .andExpect(jsonPath("$.posX").value(DEFAULT_POS_X.intValue()))
            .andExpect(jsonPath("$.posY").value(DEFAULT_POS_Y.intValue()))
            .andExpect(jsonPath("$.posZ").value(DEFAULT_POS_Z.intValue()))
            .andExpect(jsonPath("$.windSpeed").value(DEFAULT_WIND_SPEED.intValue()))
            .andExpect(jsonPath("$.windDirection").value(DEFAULT_WIND_DIRECTION));
    }

    @Test
    @Transactional
    void getNonExistingStatics() throws Exception {
        // Get the statics
        restStaticsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingStatics() throws Exception {
        // Initialize the database
        insertedStatics = staticsRepository.saveAndFlush(statics);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the statics
        Statics updatedStatics = staticsRepository.findById(statics.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedStatics are not directly saved in db
        em.detach(updatedStatics);
        updatedStatics
            .time(UPDATED_TIME)
            .posX(UPDATED_POS_X)
            .posY(UPDATED_POS_Y)
            .posZ(UPDATED_POS_Z)
            .windSpeed(UPDATED_WIND_SPEED)
            .windDirection(UPDATED_WIND_DIRECTION);

        restStaticsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedStatics.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedStatics))
            )
            .andExpect(status().isOk());

        // Validate the Statics in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedStaticsToMatchAllProperties(updatedStatics);
    }

    @Test
    @Transactional
    void putNonExistingStatics() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        statics.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStaticsMockMvc
            .perform(put(ENTITY_API_URL_ID, statics.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(statics)))
            .andExpect(status().isBadRequest());

        // Validate the Statics in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchStatics() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        statics.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStaticsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(statics))
            )
            .andExpect(status().isBadRequest());

        // Validate the Statics in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamStatics() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        statics.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStaticsMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(statics)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Statics in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateStaticsWithPatch() throws Exception {
        // Initialize the database
        insertedStatics = staticsRepository.saveAndFlush(statics);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the statics using partial update
        Statics partialUpdatedStatics = new Statics();
        partialUpdatedStatics.setId(statics.getId());

        partialUpdatedStatics.time(UPDATED_TIME).posX(UPDATED_POS_X).windSpeed(UPDATED_WIND_SPEED);

        restStaticsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStatics.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStatics))
            )
            .andExpect(status().isOk());

        // Validate the Statics in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStaticsUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedStatics, statics), getPersistedStatics(statics));
    }

    @Test
    @Transactional
    void fullUpdateStaticsWithPatch() throws Exception {
        // Initialize the database
        insertedStatics = staticsRepository.saveAndFlush(statics);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the statics using partial update
        Statics partialUpdatedStatics = new Statics();
        partialUpdatedStatics.setId(statics.getId());

        partialUpdatedStatics
            .time(UPDATED_TIME)
            .posX(UPDATED_POS_X)
            .posY(UPDATED_POS_Y)
            .posZ(UPDATED_POS_Z)
            .windSpeed(UPDATED_WIND_SPEED)
            .windDirection(UPDATED_WIND_DIRECTION);

        restStaticsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStatics.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStatics))
            )
            .andExpect(status().isOk());

        // Validate the Statics in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStaticsUpdatableFieldsEquals(partialUpdatedStatics, getPersistedStatics(partialUpdatedStatics));
    }

    @Test
    @Transactional
    void patchNonExistingStatics() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        statics.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStaticsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, statics.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(statics))
            )
            .andExpect(status().isBadRequest());

        // Validate the Statics in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchStatics() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        statics.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStaticsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(statics))
            )
            .andExpect(status().isBadRequest());

        // Validate the Statics in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamStatics() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        statics.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStaticsMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(statics)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Statics in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteStatics() throws Exception {
        // Initialize the database
        insertedStatics = staticsRepository.saveAndFlush(statics);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the statics
        restStaticsMockMvc
            .perform(delete(ENTITY_API_URL_ID, statics.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return staticsRepository.count();
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

    protected Statics getPersistedStatics(Statics statics) {
        return staticsRepository.findById(statics.getId()).orElseThrow();
    }

    protected void assertPersistedStaticsToMatchAllProperties(Statics expectedStatics) {
        assertStaticsAllPropertiesEquals(expectedStatics, getPersistedStatics(expectedStatics));
    }

    protected void assertPersistedStaticsToMatchUpdatableProperties(Statics expectedStatics) {
        assertStaticsAllUpdatablePropertiesEquals(expectedStatics, getPersistedStatics(expectedStatics));
    }
}
