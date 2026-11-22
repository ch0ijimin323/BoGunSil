package com.school.healthroom.domain.gradeclass.entity;

import com.school.healthroom.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "grade_class",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_grade_class_year_grade_class", columnNames = {"schoolYear", "grade", "classNo"})
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class GradeClass extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer schoolYear;

    @Column(nullable = false)
    private Integer grade;

    @Column(nullable = false)
    private Integer classNo;

    @Column(length = 50)
    private String teacherName;

    @Column(nullable = false)
    @Builder.Default
    private Integer studentCount = 0;

    public void update(Integer grade, Integer classNo, String teacherName, Integer studentCount) {
        this.grade = grade;
        this.classNo = classNo;
        this.teacherName = teacherName;
        this.studentCount = studentCount != null ? studentCount : 0;
    }

    public void updateTeacherAndCount(String teacherName, Integer studentCount) {
        this.teacherName = teacherName;
        this.studentCount = studentCount != null ? studentCount : 0;
    }
}
