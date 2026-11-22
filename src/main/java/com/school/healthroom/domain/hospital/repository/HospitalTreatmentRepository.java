package com.school.healthroom.domain.hospital.repository;

import com.school.healthroom.domain.hospital.entity.HospitalTreatment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface HospitalTreatmentRepository extends JpaRepository<HospitalTreatment, Long> {

    @Query("SELECT h FROM HospitalTreatment h " +
           "WHERE (:startDate IS NULL OR h.visitDate >= :startDate) " +
           "AND (:endDate IS NULL OR h.visitDate <= :endDate) " +
           "AND (:grade IS NULL OR h.grade = :grade) " +
           "AND (:keyword IS NULL OR :keyword = '' " +
           "     OR LOWER(h.studentName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "     OR LOWER(h.hospitalName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "     OR LOWER(h.symptom) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY h.visitDate DESC, h.id DESC")
    List<HospitalTreatment> search(@Param("startDate") LocalDate startDate,
                                   @Param("endDate") LocalDate endDate,
                                   @Param("grade") Integer grade,
                                   @Param("keyword") String keyword);

    @Modifying
    @Query("DELETE FROM HospitalTreatment h WHERE h.visitDate < :cutoffDate")
    int deleteByVisitDateBefore(@Param("cutoffDate") LocalDate cutoffDate);
}
