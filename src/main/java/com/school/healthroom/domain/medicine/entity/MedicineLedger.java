package com.school.healthroom.domain.medicine.entity;

import com.school.healthroom.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "medicine_ledger")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MedicineLedger extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate ledgerDate;

    @Column(nullable = false, length = 50)
    private String medicineCode;

    @Column(nullable = false, length = 100)
    private String medicineName;

    @Column(nullable = false, length = 10)
    private String ioType; // IN or OUT

    @Column(nullable = false)
    @Builder.Default
    private Integer inQuantity = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer outQuantity = 0;

    @Column(nullable = false)
    private Integer stockQuantity;

    private Long refId;

    @Column(length = 255)
    private String remark;
}
