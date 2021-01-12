package com.udacity.pricing.domain.price;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PriceRepository extends JpaRepository<Price, Long> {

    @Query(value = "SELECT * FROM Price p WHERE p.vehicleId = :vehicleId", nativeQuery = true)
    Optional<Price> findPriceById(Long vehicleId);
}
