package com.example.elimauto.repositories;

import com.example.elimauto.models.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConfigurationRepository extends JpaRepository<Configuration, String> {
    List<Configuration> findByGenerationIdOrderByDoorsCount(String generationId);
}
