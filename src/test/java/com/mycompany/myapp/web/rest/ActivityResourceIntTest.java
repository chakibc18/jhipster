package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.JhipsterApp;

import com.mycompany.myapp.domain.Activity;
import com.mycompany.myapp.repository.ActivityRepository;
import com.mycompany.myapp.service.ActivityService;
import com.mycompany.myapp.service.dto.ActivityDTO;
import com.mycompany.myapp.service.mapper.ActivityMapper;
import com.mycompany.myapp.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the ActivityResource REST controller.
 *
 * @see ActivityResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = JhipsterApp.class)
public class ActivityResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restActivityMockMvc;

    private Activity activity;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ActivityResource activityResource = new ActivityResource(activityService);
        this.restActivityMockMvc = MockMvcBuilders.standaloneSetup(activityResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Activity createEntity(EntityManager em) {
        Activity activity = new Activity()
            .name(DEFAULT_NAME);
        return activity;
    }

    @Before
    public void initTest() {
        activity = createEntity(em);
    }

    @Test
    @Transactional
    public void createActivity() throws Exception {
        int databaseSizeBeforeCreate = activityRepository.findAll().size();

        // Create the Activity
        ActivityDTO activityDTO = activityMapper.toDto(activity);
        restActivityMockMvc.perform(post("/api/activities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(activityDTO)))
            .andExpect(status().isCreated());

        // Validate the Activity in the database
        List<Activity> activityList = activityRepository.findAll();
        assertThat(activityList).hasSize(databaseSizeBeforeCreate + 1);
        Activity testActivity = activityList.get(activityList.size() - 1);
        assertThat(testActivity.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    public void createActivityWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = activityRepository.findAll().size();

        // Create the Activity with an existing ID
        activity.setId(1L);
        ActivityDTO activityDTO = activityMapper.toDto(activity);

        // An entity with an existing ID cannot be created, so this API call must fail
        restActivityMockMvc.perform(post("/api/activities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(activityDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Activity in the database
        List<Activity> activityList = activityRepository.findAll();
        assertThat(activityList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllActivities() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList
        restActivityMockMvc.perform(get("/api/activities?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(activity.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }

    @Test
    @Transactional
    public void getActivity() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get the activity
        restActivityMockMvc.perform(get("/api/activities/{id}", activity.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(activity.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingActivity() throws Exception {
        // Get the activity
        restActivityMockMvc.perform(get("/api/activities/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateActivity() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);
        int databaseSizeBeforeUpdate = activityRepository.findAll().size();

        // Update the activity
        Activity updatedActivity = activityRepository.findOne(activity.getId());
        updatedActivity
            .name(UPDATED_NAME);
        ActivityDTO activityDTO = activityMapper.toDto(updatedActivity);

        restActivityMockMvc.perform(put("/api/activities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(activityDTO)))
            .andExpect(status().isOk());

        // Validate the Activity in the database
        List<Activity> activityList = activityRepository.findAll();
        assertThat(activityList).hasSize(databaseSizeBeforeUpdate);
        Activity testActivity = activityList.get(activityList.size() - 1);
        assertThat(testActivity.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    public void updateNonExistingActivity() throws Exception {
        int databaseSizeBeforeUpdate = activityRepository.findAll().size();

        // Create the Activity
        ActivityDTO activityDTO = activityMapper.toDto(activity);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restActivityMockMvc.perform(put("/api/activities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(activityDTO)))
            .andExpect(status().isCreated());

        // Validate the Activity in the database
        List<Activity> activityList = activityRepository.findAll();
        assertThat(activityList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteActivity() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);
        int databaseSizeBeforeDelete = activityRepository.findAll().size();

        // Get the activity
        restActivityMockMvc.perform(delete("/api/activities/{id}", activity.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Activity> activityList = activityRepository.findAll();
        assertThat(activityList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Activity.class);
        Activity activity1 = new Activity();
        activity1.setId(1L);
        Activity activity2 = new Activity();
        activity2.setId(activity1.getId());
        assertThat(activity1).isEqualTo(activity2);
        activity2.setId(2L);
        assertThat(activity1).isNotEqualTo(activity2);
        activity1.setId(null);
        assertThat(activity1).isNotEqualTo(activity2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ActivityDTO.class);
        ActivityDTO activityDTO1 = new ActivityDTO();
        activityDTO1.setId(1L);
        ActivityDTO activityDTO2 = new ActivityDTO();
        assertThat(activityDTO1).isNotEqualTo(activityDTO2);
        activityDTO2.setId(activityDTO1.getId());
        assertThat(activityDTO1).isEqualTo(activityDTO2);
        activityDTO2.setId(2L);
        assertThat(activityDTO1).isNotEqualTo(activityDTO2);
        activityDTO1.setId(null);
        assertThat(activityDTO1).isNotEqualTo(activityDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(activityMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(activityMapper.fromId(null)).isNull();
    }
}
