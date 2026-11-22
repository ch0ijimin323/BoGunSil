package com.school.healthroom.domain.carestudent.repository;

import com.school.healthroom.domain.carestudent.entity.CareStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CareStudentRepository extends JpaRepository<CareStudent, Long> {

    @Query("SELECT c FROM CareStudent c " +
           "WHERE (:schoolYear IS NULL OR c.schoolYear = :schoolYear) " +
           "AND (:grade IS NULL OR c.grade = :grade) " +
           "AND (:classNo IS NULL OR c.classNo = :classNo) " +
           "AND (:keyword IS NULL OR :keyword = '' " +
           "     OR LOWER(c.studentName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "     OR LOWER(c.diseaseName) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY c.schoolYear DESC, c.grade ASC, c.classNo ASC, c.studentNo ASC")
    List<CareStudent> search(@Param("schoolYear") Integer schoolYear,
                             @Param("grade") Integer grade,
                             @Param("classNo") Integer classNo,
                             @Param("keyword") String keyword);
}
