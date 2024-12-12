package com.example.elimauto.repositories;

import com.example.elimauto.DTO.ModificationDTO;
import com.example.elimauto.models.Modification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface ModificationRepository extends JpaRepository<Modification, String> {
    ModificationDTO findByConfigurationId(String configurationId);
}
