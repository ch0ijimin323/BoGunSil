package com.school.healthroom.domain.healthlog.entity;

import com.school.healthroom.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "health_log_medicine")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class HealthLogMedicine extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "health_log_id", nullable = false)
    private HealthLog healthLog;

    @Column(nullable = false, length = 50)
    private String medicineCode;

    @Column(nullable = false, length = 100)
    private String medicineName;

    @Column(nullable = false)
    private Integer quantity;

    public void setHealthLog(HealthLog healthLog) {
        this.healthLog = healthLog;
    }
}
