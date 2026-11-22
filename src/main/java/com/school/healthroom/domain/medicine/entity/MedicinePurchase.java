package com.school.healthroom.domain.medicine.entity;

import com.school.healthroom.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "medicine_purchase")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MedicinePurchase extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate purchaseDate;

    @Column(nullable = false, length = 50)
    private String medicineCode;

    @Column(nullable = false, length = 100)
    private String medicineName;

    @Column(length = 30)
    private String unit;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Long unitPrice;

    @Column(nullable = false)
    private Long totalPrice;

    @Column(length = 100)
    private String supplier;

    private LocalDate expiryDate;

    public void update(LocalDate purchaseDate, String medicineCode, String medicineName, String unit,
                       Integer quantity, Long unitPrice, Long totalPrice, String supplier, LocalDate expiryDate) {
        this.purchaseDate = purchaseDate;
        this.medicineCode = medicineCode;
        this.medicineName = medicineName;
        this.unit = unit;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
        this.supplier = supplier;
        this.expiryDate = expiryDate;
    }
}
