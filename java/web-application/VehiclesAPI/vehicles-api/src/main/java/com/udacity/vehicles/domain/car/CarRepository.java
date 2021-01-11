package com.udacity.vehicles.domain.car;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

    @Query(value = "SELECT * FROM Car c WHERE c.id = :id", nativeQuery = true)
    Optional<Car> findCarById(Long id);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM Car c WHERE c.id = :id", nativeQuery = true)
    Integer deleteCarById(Long id);
}
