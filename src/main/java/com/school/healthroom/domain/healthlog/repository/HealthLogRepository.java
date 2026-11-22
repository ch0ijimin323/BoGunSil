package com.school.healthroom.domain.healthlog.repository;

import com.school.healthroom.domain.healthlog.entity.HealthLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface HealthLogRepository extends JpaRepository<HealthLog, Long> {

    long countByVisitDate(LocalDate visitDate);

    long countByVisitDateBetween(LocalDate startDate, LocalDate endDate);

    List<HealthLog> findTop5ByOrderByVisitDateDescVisitTimeDescIdDesc();

    List<HealthLog> findByVisitDateBefore(LocalDate cutoffDate);

    @Query("SELECT h FROM HealthLog h " +
           "WHERE (:startDate IS NULL OR h.visitDate >= :startDate) " +
           "AND (:endDate IS NULL OR h.visitDate <= :endDate) " +
           "AND (:grade IS NULL OR h.grade = :grade) " +
           "AND (:classNo IS NULL OR h.classNo = :classNo) " +
           "AND (:symptomCode IS NULL OR :symptomCode = '' OR h.symptomCode = :symptomCode) " +
           "AND (:keyword IS NULL OR :keyword = '' " +
           "     OR LOWER(h.studentName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "     OR LOWER(COALESCE(h.symptomDetail, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "     OR LOWER(COALESCE(h.treatmentDetail, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY h.visitDate DESC, h.visitTime DESC, h.id DESC")
    Page<HealthLog> searchPage(@Param("startDate") LocalDate startDate,
                               @Param("endDate") LocalDate endDate,
                               @Param("grade") Integer grade,
                               @Param("classNo") Integer classNo,
                               @Param("symptomCode") String symptomCode,
                               @Param("keyword") String keyword,
                               Pageable pageable);

    @Query("SELECT h FROM HealthLog h " +
           "WHERE (:startDate IS NULL OR h.visitDate >= :startDate) " +
           "AND (:endDate IS NULL OR h.visitDate <= :endDate) " +
           "AND (:grade IS NULL OR h.grade = :grade) " +
           "ORDER BY h.visitDate ASC, h.visitTime ASC, h.id ASC")
    List<HealthLog> searchForPrint(@Param("startDate") LocalDate startDate,
                                   @Param("endDate") LocalDate endDate,
                                   @Param("grade") Integer grade);

    @Query("SELECT h FROM HealthLog h " +
           "WHERE (:startDate IS NULL OR h.visitDate >= :startDate) " +
           "AND (:endDate IS NULL OR h.visitDate <= :endDate)")
    List<HealthLog> findByPeriod(@Param("startDate") LocalDate startDate,
                                 @Param("endDate") LocalDate endDate);
}
