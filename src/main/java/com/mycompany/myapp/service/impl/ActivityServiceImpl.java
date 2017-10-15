package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.service.ActivityService;
import com.mycompany.myapp.domain.Activity;
import com.mycompany.myapp.repository.ActivityRepository;
import com.mycompany.myapp.service.dto.ActivityDTO;
import com.mycompany.myapp.service.mapper.ActivityMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Service Implementation for managing Activity.
 */
@Service
@Transactional
public class ActivityServiceImpl implements ActivityService{

    private final Logger log = LoggerFactory.getLogger(ActivityServiceImpl.class);

    private final ActivityRepository activityRepository;

    private final ActivityMapper activityMapper;

    public ActivityServiceImpl(ActivityRepository activityRepository, ActivityMapper activityMapper) {
        this.activityRepository = activityRepository;
        this.activityMapper = activityMapper;
    }

    /**
     * Save a activity.
     *
     * @param activityDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public ActivityDTO save(ActivityDTO activityDTO) {
        log.debug("Request to save Activity : {}", activityDTO);
        Activity activity = activityMapper.toEntity(activityDTO);
        activity = activityRepository.save(activity);
        return activityMapper.toDto(activity);
    }

    /**
     *  Get all the activities.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ActivityDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Activities");
        return activityRepository.findAll(pageable)
            .map(activityMapper::toDto);
    }

    /**
     *  Get one activity by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public ActivityDTO findOne(Long id) {
        log.debug("Request to get Activity : {}", id);
        Activity activity = activityRepository.findOneWithEagerRelationships(id);
        return activityMapper.toDto(activity);
    }

    /**
     *  Delete the  activity by id.
     *
     *  @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Activity : {}", id);
        activityRepository.delete(id);
    }
}
