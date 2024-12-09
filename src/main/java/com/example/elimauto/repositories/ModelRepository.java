package com.example.elimauto.repositories;

import com.example.elimauto.models.Model;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModelRepository extends JpaRepository<Model, String> {
    List<Model> findByMarkIdOrderByNameAsc(String markId);
}
