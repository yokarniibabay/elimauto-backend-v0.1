package com.example.elimauto.repositories;

import com.example.elimauto.models.Generation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GenerationRepository extends JpaRepository<Generation, String> {
    List<Generation> findByModelIdOrderByYearStart(String modelId);
}
