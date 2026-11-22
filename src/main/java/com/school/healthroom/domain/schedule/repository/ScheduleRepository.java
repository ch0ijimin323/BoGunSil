package com.school.healthroom.domain.schedule.repository;

import com.school.healthroom.domain.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    @Query("SELECT s FROM Schedule s " +
           "WHERE (:startDate IS NULL OR s.endDate >= :startDate) " +
           "AND (:endDate IS NULL OR s.startDate <= :endDate) " +
           "AND (:keyword IS NULL OR :keyword = '' " +
           "     OR LOWER(s.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "     OR LOWER(COALESCE(s.content, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY s.startDate ASC, s.startTime ASC, s.id DESC")
    List<Schedule> search(@Param("startDate") LocalDate startDate,
                          @Param("endDate") LocalDate endDate,
                          @Param("keyword") String keyword);

    @Query("SELECT s FROM Schedule s " +
           "WHERE s.startDate <= :monthEnd AND s.endDate >= :monthStart " +
           "ORDER BY s.startDate ASC, s.startTime ASC, s.id ASC")
    List<Schedule> findOverlappingPeriod(@Param("monthStart") LocalDate monthStart,
                                         @Param("monthEnd") LocalDate monthEnd);

    @Query("SELECT s FROM Schedule s " +
           "WHERE s.startDate <= :today AND s.endDate >= :today " +
           "ORDER BY s.startTime ASC, s.id ASC")
    List<Schedule> findTodaySchedules(@Param("today") LocalDate today);
}
