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


    //Поиск
    @Query("SELECT a FROM Announcement a " +
            "WHERE (:markId IS NULL OR a.makeId = :markId) " +
            "AND (:modelId IS NULL OR a.modelId = :modelId) " +
            "AND (:generationId IS NULL OR a.generationId = :generationId) " +
            "AND (:minVolume IS NULL OR a.engineCapacity >= :minVolume) " +
            "AND (:maxVolume IS NULL OR a.engineCapacity <= :maxVolume) " +
            "AND (:minYear IS NULL OR a.year >= :minYear) " +
            "AND (:maxYear IS NULL OR a.year <= :maxYear) " +
            "AND (:minMileage IS NULL OR a.mileage >= :minMileage) " +
            "AND (:maxMileage IS NULL OR a.mileage <= :maxMileage) " +
            "AND (:minPrice IS NULL OR a.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR a.price <= :maxPrice) " +
            "AND (:city IS NULL OR a.city = :city)")
    List<Announcement> searchAnnouncements(@Param("markId") String markId,
                                           @Param("modelId") String modelId,
                                           @Param("generationId") String generationId,
                                           @Param("minVolume") Double minVolume,
                                           @Param("maxVolume") Double maxVolume,
                                           @Param("minYear") Integer minYear,
                                           @Param("maxYear") Integer maxYear,
                                           @Param("minMileage") Integer minMileage,
                                           @Param("maxMileage") Integer maxMileage,
                                           @Param("minPrice") Double minPrice,
                                           @Param("maxPrice") Double maxPrice,
                                           @Param("city") String city);
}
