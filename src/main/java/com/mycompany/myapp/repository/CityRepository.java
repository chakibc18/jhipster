package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.City;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.List;

/**
 * Spring Data JPA repository for the City entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CityRepository extends JpaRepository<City, Long> {
    @Query("select distinct city from City city left join fetch city.users")
    List<City> findAllWithEagerRelationships();

    @Query("select city from City city left join fetch city.users where city.id =:id")
    City findOneWithEagerRelationships(@Param("id") Long id);

}
