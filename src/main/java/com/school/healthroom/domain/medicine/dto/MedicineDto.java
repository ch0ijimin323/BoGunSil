package com.school.healthroom.domain.medicine.dto;

import com.school.healthroom.domain.medicine.entity.MedicineLedger;
import com.school.healthroom.domain.medicine.entity.MedicinePurchase;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class MedicineDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PurchaseSaveRequest {
        @NotNull(message = "구입일자는 필수입니다.")
        private LocalDate purchaseDate;

        @NotBlank(message = "약품코드는 필수입니다.")
        private String medicineCode;

        @NotBlank(message = "약품명은 필수입니다.")
        private String medicineName;

        private String unit;

        @NotNull(message = "구입 수량은 필수입니다.")
        @Min(value = 1, message = "구입 수량은 1 이상이어야 합니다.")
        private Integer quantity;

        @NotNull(message = "단가는 필수입니다.")
        @Min(value = 0, message = "단가는 0 이상이어야 합니다.")
        private Long unitPrice;

        private Long totalPrice;
        private String supplier;
        private LocalDate expiryDate;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PurchaseResponse {
        private Long id;
        private LocalDate purchaseDate;
        private String medicineCode;
        private String medicineName;
        private String unit;
        private Integer quantity;
        private Long unitPrice;
        private Long totalPrice;
        private String supplier;
        private LocalDate expiryDate;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static PurchaseResponse from(MedicinePurchase entity) {
            return PurchaseResponse.builder()
                    .id(entity.getId())
                    .purchaseDate(entity.getPurchaseDate())
                    .medicineCode(entity.getMedicineCode())
                    .medicineName(entity.getMedicineName())
                    .unit(entity.getUnit())
                    .quantity(entity.getQuantity())
                    .unitPrice(entity.getUnitPrice())
                    .totalPrice(entity.getTotalPrice())
                    .supplier(entity.getSupplier())
                    .expiryDate(entity.getExpiryDate())
                    .createdAt(entity.getCreatedAt())
                    .updatedAt(entity.getUpdatedAt())
                    .build();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LedgerResponse {
        private Long id;
        private LocalDate ledgerDate;
        private String medicineCode;
        private String medicineName;
        private String ioType;
        private Integer inQuantity;
        private Integer outQuantity;
        private Integer stockQuantity;
        private Long refId;
        private String remark;
        private LocalDateTime createdAt;

        public static LedgerResponse from(MedicineLedger entity) {
            return LedgerResponse.builder()
                    .id(entity.getId())
                    .ledgerDate(entity.getLedgerDate())
                    .medicineCode(entity.getMedicineCode())
                    .medicineName(entity.getMedicineName())
                    .ioType(entity.getIoType())
                    .inQuantity(entity.getInQuantity())
                    .outQuantity(entity.getOutQuantity())
                    .stockQuantity(entity.getStockQuantity())
                    .refId(entity.getRefId())
                    .remark(entity.getRemark())
                    .createdAt(entity.getCreatedAt())
                    .build();
        }
    }
}
