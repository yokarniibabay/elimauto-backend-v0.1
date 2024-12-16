package com.example.elimauto.repositories;

import com.example.elimauto.elimauto.consts.AnnouncementStatus;
import com.example.elimauto.models.Announcement;
import com.example.elimauto.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    List<Announcement> findByTitle(String title);
    List<Announcement> findByAuthor(User user);
    List<Announcement> findByAuthorId(Long id);
    List<Announcement> findByAuthorIdAndStatus(Long authorId, AnnouncementStatus status);
    List<Announcement> findByStatus(AnnouncementStatus status);
    List<Announcement> findAllByStatusAndRejectedAtBefore(AnnouncementStatus status, LocalDateTime dateTime);
    List<Announcement> findAllByOrderByCreatedAtDesc();


    @Modifying
    @Transactional
    @Query("UPDATE Announcement a SET a.views = a.views + 1 WHERE a.id = :id")
    void incrementViews(@Param("id") Long id);
}
