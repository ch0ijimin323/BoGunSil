package com.school.healthroom.domain.healthlog.repository;

import com.school.healthroom.domain.healthlog.entity.HealthLogMedicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface HealthLogMedicineRepository extends JpaRepository<HealthLogMedicine, Long> {

    @Query("SELECT hm FROM HealthLogMedicine hm JOIN FETCH hm.healthLog h " +
           "WHERE (:startDate IS NULL OR h.visitDate >= :startDate) " +
           "AND (:endDate IS NULL OR h.visitDate <= :endDate)")
    List<HealthLogMedicine> findByPeriod(@Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate);
}
