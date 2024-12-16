package com.example.elimauto.repositories;


import com.example.elimauto.models.Modification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ModificationRepository extends JpaRepository<Modification, String> {
    List<Modification> findByConfigurationId(String configurationId);
}
