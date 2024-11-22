package com.example.elimauto.repositories;

import com.example.elimauto.models.Announcement;
import com.example.elimauto.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    List<Announcement> findByTitle(String title);
    List<Announcement> findByAuthor(User user);
}
