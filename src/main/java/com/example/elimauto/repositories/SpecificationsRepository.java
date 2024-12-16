package com.example.elimauto.repositories;

import com.example.elimauto.models.Specifications;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpecificationsRepository extends JpaRepository<Specifications, String> {

    // Поиск спецификаций по идентификатору комплектации
    Specifications findByComplectationId(String complectationId);

    // Получение уникальных значений объема двигателя (в литрах) и трансмиссии для списка complectationId
    @Query("SELECT DISTINCT s.volumeLitres, s.transmission " +
            "FROM Specifications s WHERE s.complectationId IN :complectationIds")
    List<Object[]> findDistinctVolumeLitresAndTransmission(@Param("complectationIds") List<String> complectationIds);

    // Получение уникальных объемов двигателя (в литрах) для списка complectationId
    @Query("SELECT DISTINCT s.volumeLitres " +
            "FROM Specifications s WHERE s.complectationId IN :complectationIds")
    List<String> findDistinctVolumeLitres(@Param("complectationIds") List<String> complectationIds);

    // Получение уникальных типов трансмиссий для заданного объема двигателя (в литрах)
    @Query("SELECT DISTINCT s.transmission " +
            "FROM Specifications s WHERE s.complectationId IN :complectationIds AND s.volumeLitres = :volumeLitres")
    List<String> findDistinctTransmissions(@Param("complectationIds") List<String> complectationIds,
                                           @Param("volumeLitres") String volumeLitres);

    // Получение уникальных типов привода для объема двигателя (в литрах) и трансмиссии
    @Query("SELECT DISTINCT s.drive " +
            "FROM Specifications s WHERE s.complectationId IN :complectationIds " +
            "AND s.volumeLitres = :volumeLitres AND s.transmission = :transmission")
    List<String> findDistinctDrives(@Param("complectationIds") List<String> complectationIds,
                                    @Param("volumeLitres") String volumeLitres,
                                    @Param("transmission") String transmission);

    // Получение уникальных значений мощности двигателя для объема двигателя, трансмиссии и типа привода
    @Query("SELECT DISTINCT s.horsePower " +
            "FROM Specifications s WHERE s.complectationId IN :complectationIds " +
            "AND s.volumeLitres = :volumeLitres AND s.transmission = :transmission AND s.drive = :drive")
    List<String> findDistinctHorsePowers(@Param("complectationIds") List<String> complectationIds,
                                         @Param("volumeLitres") String volumeLitres,
                                         @Param("transmission") String transmission,
                                         @Param("drive") String drive);

    // Поиск спецификаций по всем параметрам
    @Query("SELECT s FROM Specifications s WHERE s.complectationId IN :complectationIds " +
            "AND s.volumeLitres = :volumeLitres AND s.transmission = :transmission AND s.drive = :drive " +
            "AND s.horsePower = :horsePower")
    List<Specifications> findSpecifications(@Param("complectationIds") List<String> complectationIds,
                                            @Param("volumeLitres") String volumeLitres,
                                            @Param("transmission") String transmission,
                                            @Param("drive") String drive,
                                            @Param("horsePower") String horsePower);
}
