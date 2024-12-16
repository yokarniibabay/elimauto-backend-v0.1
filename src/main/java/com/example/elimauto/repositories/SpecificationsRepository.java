package com.example.elimauto.repositories;

import com.example.elimauto.models.Specifications;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpecificationsRepository extends JpaRepository<Specifications, String> {
    Specifications findByComplectationId(String complectationId);
    List<Specifications> findByEngineCapacityAndTransmission(Double engineCapacity, String transmission);

    @Query("SELECT DISTINCT s.engineCapacity FROM Specification s WHERE s.configuration.id = :configurationId")
    List<String> findDistinctEngineCapacities(@Param("configurationId") String configurationId);

    @Query("SELECT DISTINCT s.transmission FROM Specification s WHERE s.configuration.id = :configurationId AND s.engineCapacity = :engineCapacity")
    List<String> findDistinctTransmissions(@Param("configurationId") String configurationId, @Param("engineCapacity") String engineCapacity);

    @Query("SELECT DISTINCT s.driveType FROM Specification s WHERE s.configuration.id = :configurationId AND s.engineCapacity = :engineCapacity AND s.transmission = :transmission")
    List<String> findDistinctDriveTypes(@Param("configurationId") String configurationId, @Param("engineCapacity") String engineCapacity, @Param("transmission") String transmission);

    @Query("SELECT DISTINCT s.horsepower FROM Specification s WHERE s.configuration.id = :configurationId AND s.engineCapacity = :engineCapacity AND s.transmission = :transmission AND s.driveType = :driveType")
    List<String> findDistinctHorsepowers(@Param("configurationId") String configurationId, @Param("engineCapacity") String engineCapacity, @Param("transmission") String transmission, @Param("driveType") String driveType);
}
