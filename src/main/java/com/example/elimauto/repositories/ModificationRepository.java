package com.example.elimauto.repositories;


import com.example.elimauto.models.Modification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ModificationRepository extends JpaRepository<Modification, String> {
    List<Modification> findByConfigurationId(String configurationId);

    @Query("SELECT m.complectationId FROM Modification m WHERE m.configuration.id = :configurationId")
    List<String> findComplectationIdsByConfigurationId(@Param("configurationId") String configurationId);

    @Query("""
        SELECT DISTINCT mod.groupName, mod.configurationId
        FROM Modification mod
        JOIN mod.configuration conf
        JOIN conf.generation gen
        WHERE gen.model.id = :modelId
    """)
    List<Object[]> findGroupsByModelId(@Param("modelId") String modelId);
}
