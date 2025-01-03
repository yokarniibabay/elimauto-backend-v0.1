package com.example.elimauto.repositories;

import com.example.elimauto.models.Mark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MarkRepository extends JpaRepository<Mark, String> {
    List<Mark> findAllByOrderByNameAsc();
    List<Mark> findAllByOrderByPopularDescNameAsc();
}
