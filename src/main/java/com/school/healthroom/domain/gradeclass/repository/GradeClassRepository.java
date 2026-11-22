package com.school.healthroom.domain.gradeclass.repository;

import com.school.healthroom.domain.gradeclass.entity.GradeClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GradeClassRepository extends JpaRepository<GradeClass, Long> {

    boolean existsBySchoolYearAndGradeAndClassNo(Integer schoolYear, Integer grade, Integer classNo);

    boolean existsBySchoolYearAndGradeAndClassNoAndIdNot(Integer schoolYear, Integer grade, Integer classNo, Long id);

    Optional<GradeClass> findBySchoolYearAndGradeAndClassNo(Integer schoolYear, Integer grade, Integer classNo);

    @Query("SELECT g FROM GradeClass g " +
           "WHERE (:schoolYear IS NULL OR g.schoolYear = :schoolYear) " +
           "AND (:grade IS NULL OR g.grade = :grade) " +
           "ORDER BY g.schoolYear DESC, g.grade ASC, g.classNo ASC")
    List<GradeClass> search(@Param("schoolYear") Integer schoolYear, @Param("grade") Integer grade);

    List<GradeClass> findBySchoolYearOrderByGradeAscClassNoAsc(Integer schoolYear);
}
