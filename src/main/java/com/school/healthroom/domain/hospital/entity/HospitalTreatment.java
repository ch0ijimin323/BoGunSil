package com.school.healthroom.domain.hospital.entity;

import com.school.healthroom.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "hospital_treatment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class HospitalTreatment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate visitDate;

    @Column(nullable = false)
    private Integer grade;

    @Column(nullable = false)
    private Integer classNo;

    @Column(nullable = false)
    private Integer studentNo;

    @Column(nullable = false, length = 50)
    private String studentName;

    @Column(nullable = false, length = 255)
    private String symptom;

    @Column(nullable = false, length = 150)
    private String hospitalName;

    @Column(length = 100)
    private String companion;

    @Column(columnDefinition = "TEXT")
    private String treatmentResult;

    public void update(LocalDate visitDate, Integer grade, Integer classNo, Integer studentNo,
                       String studentName, String symptom, String hospitalName, String companion, String treatmentResult) {
        this.visitDate = visitDate;
        this.grade = grade;
        this.classNo = classNo;
        this.studentNo = studentNo;
        this.studentName = studentName;
        this.symptom = symptom;
        this.hospitalName = hospitalName;
        this.companion = companion;
        this.treatmentResult = treatmentResult;
    }
}
