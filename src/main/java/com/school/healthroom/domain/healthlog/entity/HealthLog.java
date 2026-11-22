package com.school.healthroom.domain.healthlog.entity;

import com.school.healthroom.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "health_log")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class HealthLog extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate visitDate;

    @Column(nullable = false)
    private LocalTime visitTime;

    @Column(nullable = false)
    private Integer grade;

    @Column(nullable = false)
    private Integer classNo;

    @Column(nullable = false)
    private Integer studentNo;

    @Column(nullable = false, length = 50)
    private String studentName;

    @Column(length = 10)
    private String gender;

    @Column(nullable = false, length = 50)
    private String symptomCode;

    @Column(length = 255)
    private String symptomDetail;

    @Column(length = 50)
    private String treatmentCode;

    @Column(length = 255)
    private String treatmentDetail;

    @Column(nullable = false, length = 20)
    private String sourceType; // ADMIN or KIOSK

    @Column(columnDefinition = "TEXT")
    private String memo;

    @OneToMany(mappedBy = "healthLog", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<HealthLogMedicine> medicines = new ArrayList<>();

    public void addMedicine(HealthLogMedicine medicine) {
        this.medicines.add(medicine);
        medicine.setHealthLog(this);
    }

    public void clearMedicines() {
        this.medicines.clear();
    }

    public void update(LocalDate visitDate, LocalTime visitTime, Integer grade, Integer classNo,
                       Integer studentNo, String studentName, String gender, String symptomCode,
                       String symptomDetail, String treatmentCode, String treatmentDetail, String memo) {
        this.visitDate = visitDate;
        this.visitTime = visitTime;
        this.grade = grade;
        this.classNo = classNo;
        this.studentNo = studentNo;
        this.studentName = studentName;
        this.gender = gender;
        this.symptomCode = symptomCode;
        this.symptomDetail = symptomDetail;
        this.treatmentCode = treatmentCode;
        this.treatmentDetail = treatmentDetail;
        this.memo = memo;
    }

    public void changeVisitDate(LocalDate targetDate) {
        this.visitDate = targetDate;
    }
}
