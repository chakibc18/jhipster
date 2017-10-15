package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.City;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;
import java.util.List;

/**
 * Spring Data JPA repository for the City entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CityRepository extends JpaRepository<City, Long> {

    @Query("select city from City city where city.user.login = ?#{principal.username}")
    List<City> findByUserIsCurrentUser();

}
