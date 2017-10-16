package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Activity;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.List;

/**
 * Spring Data JPA repository for the Activity entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {
    @Query("select distinct activity from Activity activity left join fetch activity.users")
    List<Activity> findAllWithEagerRelationships();

    @Query("select activity from Activity activity left join fetch activity.users where activity.id =:id")
    Activity findOneWithEagerRelationships(@Param("id") Long id);

}
