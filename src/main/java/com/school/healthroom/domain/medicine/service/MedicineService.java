package com.school.healthroom.domain.medicine.service;

import com.school.healthroom.domain.medicine.dto.MedicineDto;
import com.school.healthroom.domain.medicine.entity.MedicineLedger;
import com.school.healthroom.domain.medicine.entity.MedicinePurchase;
import com.school.healthroom.domain.medicine.repository.MedicineLedgerRepository;
import com.school.healthroom.domain.medicine.repository.MedicinePurchaseRepository;
import com.school.healthroom.global.error.CustomException;
import com.school.healthroom.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MedicineService {

    private final MedicinePurchaseRepository medicinePurchaseRepository;
    private final MedicineLedgerRepository medicineLedgerRepository;

    public List<MedicineDto.PurchaseResponse> getPurchases(LocalDate startDate, LocalDate endDate, String keyword) {
        return medicinePurchaseRepository.search(startDate, endDate, keyword)
                .stream()
                .map(MedicineDto.PurchaseResponse::from)
                .toList();
    }

    public MedicineDto.PurchaseResponse getPurchase(Long id) {
        MedicinePurchase purchase = medicinePurchaseRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "약품 구입 내역을 찾을 수 없습니다."));
        return MedicineDto.PurchaseResponse.from(purchase);
    }

    @Transactional
    public MedicineDto.PurchaseResponse createPurchase(MedicineDto.PurchaseSaveRequest request) {
        long totalPrice = request.getTotalPrice() != null
                ? request.getTotalPrice()
                : request.getUnitPrice() * request.getQuantity();

        MedicinePurchase purchase = MedicinePurchase.builder()
                .purchaseDate(request.getPurchaseDate())
                .medicineCode(request.getMedicineCode())
                .medicineName(request.getMedicineName())
                .unit(request.getUnit())
                .quantity(request.getQuantity())
                .unitPrice(request.getUnitPrice())
                .totalPrice(totalPrice)
                .supplier(request.getSupplier())
                .expiryDate(request.getExpiryDate())
                .build();

        MedicinePurchase saved = medicinePurchaseRepository.save(purchase);

        recordStockIn(
                saved.getPurchaseDate(),
                saved.getMedicineCode(),
                saved.getMedicineName(),
                saved.getQuantity(),
                saved.getId(),
                "약품 구입 입고" + (saved.getSupplier() != null ? " (" + saved.getSupplier() + ")" : "")
        );

        return MedicineDto.PurchaseResponse.from(saved);
    }

    @Transactional
    public MedicineDto.PurchaseResponse updatePurchase(Long id, MedicineDto.PurchaseSaveRequest request) {
        MedicinePurchase purchase = medicinePurchaseRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "약품 구입 내역을 찾을 수 없습니다."));

        String oldCode = purchase.getMedicineCode();
        String oldName = purchase.getMedicineName();
        int oldQty = purchase.getQuantity();

        String newCode = request.getMedicineCode();
        String newName = request.getMedicineName();
        int newQty = request.getQuantity();

        if (!oldCode.equals(newCode)) {
            recordStockOut(
                    request.getPurchaseDate(),
                    oldCode,
                    oldName,
                    oldQty,
                    purchase.getId(),
                    "약품 구입 수정(약품 변경)에 따른 기존 입고 차감"
            );
            recordStockIn(
                    request.getPurchaseDate(),
                    newCode,
                    newName,
                    newQty,
                    purchase.getId(),
                    "약품 구입 수정(약품 변경)에 따른 신규 입고"
            );
        } else {
            int diff = newQty - oldQty;
            if (diff > 0) {
                recordStockIn(
                        request.getPurchaseDate(),
                        newCode,
                        newName,
                        diff,
                        purchase.getId(),
                        "약품 구입 수량 증감 보정 (+" + diff + ")"
                );
            } else if (diff < 0) {
                recordStockOut(
                        request.getPurchaseDate(),
                        newCode,
                        newName,
                        Math.abs(diff),
                        purchase.getId(),
                        "약품 구입 수량 증감 보정 (" + diff + ")"
                );
            }
        }

        long totalPrice = request.getTotalPrice() != null
                ? request.getTotalPrice()
                : request.getUnitPrice() * request.getQuantity();

        purchase.update(
                request.getPurchaseDate(),
                request.getMedicineCode(),
                request.getMedicineName(),
                request.getUnit(),
                request.getQuantity(),
                request.getUnitPrice(),
                totalPrice,
                request.getSupplier(),
                request.getExpiryDate()
        );

        return MedicineDto.PurchaseResponse.from(purchase);
    }

    @Transactional
    public void deletePurchase(Long id) {
        MedicinePurchase purchase = medicinePurchaseRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "약품 구입 내역을 찾을 수 없습니다."));

        recordStockOut(
                LocalDate.now(),
                purchase.getMedicineCode(),
                purchase.getMedicineName(),
                purchase.getQuantity(),
                purchase.getId(),
                "약품 구입 내역 삭제에 따른 입고 차감 보정"
        );

        medicinePurchaseRepository.delete(purchase);
    }

    public List<MedicineDto.LedgerResponse> getLedgers(LocalDate startDate, LocalDate endDate, String medicineCode) {
        return medicineLedgerRepository.search(startDate, endDate, medicineCode)
                .stream()
                .map(MedicineDto.LedgerResponse::from)
                .toList();
    }

    private int getCurrentStock(String medicineCode) {
        return medicineLedgerRepository.findTopByMedicineCodeOrderByIdDesc(medicineCode)
                .map(MedicineLedger::getStockQuantity)
                .orElse(0);
    }

    private void recordStockIn(LocalDate ledgerDate, String medicineCode, String medicineName,
                               int quantity, Long refId, String remark) {
        int currentStock = getCurrentStock(medicineCode);
        MedicineLedger ledger = MedicineLedger.builder()
                .ledgerDate(ledgerDate)
                .medicineCode(medicineCode)
                .medicineName(medicineName)
                .ioType("IN")
                .inQuantity(quantity)
                .outQuantity(0)
                .stockQuantity(currentStock + quantity)
                .refId(refId)
                .remark(remark)
                .build();
        medicineLedgerRepository.save(ledger);
    }

    private void recordStockOut(LocalDate ledgerDate, String medicineCode, String medicineName,
                                int quantity, Long refId, String remark) {
        int currentStock = getCurrentStock(medicineCode);
        if (currentStock < quantity) {
            throw new CustomException(
                    ErrorCode.INSUFFICIENT_STOCK,
                    String.format("약품 [%s]의 잔여 재고가 부족하여 차감할 수 없습니다. (현재 재고: %d, 차감 수량: %d)",
                            medicineName, currentStock, quantity)
            );
        }

        MedicineLedger ledger = MedicineLedger.builder()
                .ledgerDate(ledgerDate)
                .medicineCode(medicineCode)
                .medicineName(medicineName)
                .ioType("OUT")
                .inQuantity(0)
                .outQuantity(quantity)
                .stockQuantity(currentStock - quantity)
                .refId(refId)
                .remark(remark)
                .build();
        medicineLedgerRepository.save(ledger);
    }
}
