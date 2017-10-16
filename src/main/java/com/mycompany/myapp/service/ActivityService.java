package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Activity;
import com.mycompany.myapp.repository.ActivityRepository;
import com.mycompany.myapp.service.dto.ActivityDTO;
import com.mycompany.myapp.service.mapper.ActivityMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing Activity.
 */
@Service
@Transactional
public class ActivityService {

    private final Logger log = LoggerFactory.getLogger(ActivityService.class);

    private final ActivityRepository activityRepository;

    private final ActivityMapper activityMapper;

    public ActivityService(ActivityRepository activityRepository, ActivityMapper activityMapper) {
        this.activityRepository = activityRepository;
        this.activityMapper = activityMapper;
    }

    /**
     * Save a activity.
     *
     * @param activityDTO the entity to save
     * @return the persisted entity
     */
    public ActivityDTO save(ActivityDTO activityDTO) {
        log.debug("Request to save Activity : {}", activityDTO);
        Activity activity = activityMapper.toEntity(activityDTO);
        activity = activityRepository.save(activity);
        return activityMapper.toDto(activity);
    }

    /**
     *  Get all the activities.
     *
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<ActivityDTO> findAll() {
        log.debug("Request to get all Activities");
        return activityRepository.findAllWithEagerRelationships().stream()
            .map(activityMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     *  Get one activity by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
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
    public void delete(Long id) {
        log.debug("Request to delete Activity : {}", id);
        activityRepository.delete(id);
    }
}
