package com.school.healthroom.domain.carestudent.entity;

import com.school.healthroom.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "care_student")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CareStudent extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer schoolYear;

    @Column(nullable = false)
    private Integer grade;

    @Column(nullable = false)
    private Integer classNo;

    @Column(nullable = false)
    private Integer studentNo;

    @Column(nullable = false, length = 50)
    private String studentName;

    @Column(nullable = false, length = 150)
    private String diseaseName;

    @Column(length = 100)
    private String emergencyContact;

    @Column(columnDefinition = "TEXT")
    private String careGuide;

    public void update(Integer schoolYear, Integer grade, Integer classNo, Integer studentNo,
                       String studentName, String diseaseName, String emergencyContact, String careGuide) {
        this.schoolYear = schoolYear;
        this.grade = grade;
        this.classNo = classNo;
        this.studentNo = studentNo;
        this.studentName = studentName;
        this.diseaseName = diseaseName;
        this.emergencyContact = emergencyContact;
        this.careGuide = careGuide;
    }
}
