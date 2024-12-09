package com.example.elimauto.repositories;

import com.example.elimauto.models.Generation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GenerationRepository extends JpaRepository<Generation, String> {
}
