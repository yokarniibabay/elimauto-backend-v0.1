package com.example.elimauto.repositories;

import com.example.elimauto.models.Specifications;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpecificationsRepository extends JpaRepository<Specifications, String> {
}
