package com.school.healthroom.domain.medicine.controller;

import com.school.healthroom.domain.medicine.dto.MedicineDto;
import com.school.healthroom.domain.medicine.service.MedicineService;
import com.school.healthroom.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class MedicineController {

    private final MedicineService medicineService;

    @GetMapping("/medicine-purchases")
    public ResponseEntity<ApiResponse<List<MedicineDto.PurchaseResponse>>> getPurchases(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(ApiResponse.ok(medicineService.getPurchases(startDate, endDate, keyword)));
    }

    @GetMapping("/medicine-purchases/{id}")
    public ResponseEntity<ApiResponse<MedicineDto.PurchaseResponse>> getPurchase(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(medicineService.getPurchase(id)));
    }

    @PostMapping("/medicine-purchases")
    public ResponseEntity<ApiResponse<MedicineDto.PurchaseResponse>> createPurchase(
            @Valid @RequestBody MedicineDto.PurchaseSaveRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(medicineService.createPurchase(request)));
    }

    @PutMapping("/medicine-purchases/{id}")
    public ResponseEntity<ApiResponse<MedicineDto.PurchaseResponse>> updatePurchase(
            @PathVariable Long id,
            @Valid @RequestBody MedicineDto.PurchaseSaveRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(medicineService.updatePurchase(id, request)));
    }

    @DeleteMapping("/medicine-purchases/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePurchase(@PathVariable Long id) {
        medicineService.deletePurchase(id);
        return ResponseEntity.ok(ApiResponse.okMessage("약품 구입 내역이 삭제되었습니다."));
    }

    @GetMapping("/medicine-ledgers")
    public ResponseEntity<ApiResponse<List<MedicineDto.LedgerResponse>>> getLedgers(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String medicineCode) {
        return ResponseEntity.ok(ApiResponse.ok(medicineService.getLedgers(startDate, endDate, medicineCode)));
    }
}
