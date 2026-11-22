package com.school.healthroom.domain.healthlog.service;

import com.school.healthroom.domain.healthlog.dto.HealthLogDto;
import com.school.healthroom.domain.healthlog.entity.HealthLog;
import com.school.healthroom.domain.healthlog.entity.HealthLogMedicine;
import com.school.healthroom.domain.healthlog.repository.HealthLogRepository;
import com.school.healthroom.domain.medicine.entity.MedicineLedger;
import com.school.healthroom.domain.medicine.repository.MedicineLedgerRepository;
import com.school.healthroom.global.error.CustomException;
import com.school.healthroom.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HealthLogService {

    private final HealthLogRepository healthLogRepository;
    private final MedicineLedgerRepository medicineLedgerRepository;

    public HealthLogDto.PageResponse getHealthLogs(LocalDate startDate, LocalDate endDate,
                                                   Integer grade, Integer classNo,
                                                   String symptomCode, String keyword,
                                                   int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1));
        Page<HealthLog> result = healthLogRepository.searchPage(
                startDate, endDate, grade, classNo, symptomCode, keyword, pageable
        );
        return HealthLogDto.PageResponse.from(result);
    }

    public HealthLogDto.Response getHealthLog(Long id) {
        HealthLog healthLog = healthLogRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "보건일지를 찾을 수 없습니다."));
        return HealthLogDto.Response.from(healthLog);
    }

    @Transactional
    public HealthLogDto.Response createHealthLog(HealthLogDto.SaveRequest request) {
        LocalTime visitTime = request.getVisitTime() != null ? request.getVisitTime() : LocalTime.now();

        HealthLog healthLog = HealthLog.builder()
                .visitDate(request.getVisitDate())
                .visitTime(visitTime)
                .grade(request.getGrade())
                .classNo(request.getClassNo())
                .studentNo(request.getStudentNo())
                .studentName(request.getStudentName())
                .gender(request.getGender())
                .symptomCode(request.getSymptomCode())
                .symptomDetail(request.getSymptomDetail())
                .treatmentCode(request.getTreatmentCode())
                .treatmentDetail(request.getTreatmentDetail())
                .sourceType("ADMIN")
                .memo(request.getMemo())
                .build();

        if (request.getMedicines() != null) {
            for (HealthLogDto.MedicineItemRequest item : request.getMedicines()) {
                healthLog.addMedicine(HealthLogMedicine.builder()
                        .medicineCode(item.getMedicineCode())
                        .medicineName(item.getMedicineName())
                        .quantity(item.getQuantity())
                        .build());
            }
        }

        HealthLog saved = healthLogRepository.save(healthLog);

        if (request.getMedicines() != null) {
            for (HealthLogDto.MedicineItemRequest item : request.getMedicines()) {
                recordStockOut(
                        saved.getVisitDate(),
                        item.getMedicineCode(),
                        item.getMedicineName(),
                        item.getQuantity(),
                        saved.getId(),
                        "보건일지 투약 출고 (" + saved.getStudentName() + ")"
                );
            }
        }

        return HealthLogDto.Response.from(saved);
    }

    @Transactional
    public HealthLogDto.Response updateHealthLog(Long id, HealthLogDto.SaveRequest request) {
        HealthLog healthLog = healthLogRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "보건일지를 찾을 수 없습니다."));

        // 1. 기존 투약 약품 출고량을 IN(수정원복) 처리
        if (healthLog.getMedicines() != null && !healthLog.getMedicines().isEmpty()) {
            for (HealthLogMedicine oldMed : healthLog.getMedicines()) {
                recordStockIn(
                        request.getVisitDate(),
                        oldMed.getMedicineCode(),
                        oldMed.getMedicineName(),
                        oldMed.getQuantity(),
                        healthLog.getId(),
                        "보건일지 수정에 따른 재고 원복 (" + healthLog.getStudentName() + ")"
                );
            }
        }

        // 2. 일지 기본 정보 및 약품 목록 갱신
        LocalTime visitTime = request.getVisitTime() != null ? request.getVisitTime() : healthLog.getVisitTime();
        healthLog.update(
                request.getVisitDate(),
                visitTime,
                request.getGrade(),
                request.getClassNo(),
                request.getStudentNo(),
                request.getStudentName(),
                request.getGender(),
                request.getSymptomCode(),
                request.getSymptomDetail(),
                request.getTreatmentCode(),
                request.getTreatmentDetail(),
                request.getMemo()
        );

        healthLog.clearMedicines();
        if (request.getMedicines() != null) {
            for (HealthLogDto.MedicineItemRequest item : request.getMedicines()) {
                healthLog.addMedicine(HealthLogMedicine.builder()
                        .medicineCode(item.getMedicineCode())
                        .medicineName(item.getMedicineName())
                        .quantity(item.getQuantity())
                        .build());
            }
        }

        // 3. 변경된 약품 목록 기준으로 재출고 기록
        if (request.getMedicines() != null) {
            for (HealthLogDto.MedicineItemRequest item : request.getMedicines()) {
                recordStockOut(
                        healthLog.getVisitDate(),
                        item.getMedicineCode(),
                        item.getMedicineName(),
                        item.getQuantity(),
                        healthLog.getId(),
                        "보건일지 수정 재출고 (" + healthLog.getStudentName() + ")"
                );
            }
        }

        return HealthLogDto.Response.from(healthLog);
    }

    @Transactional
    public void deleteHealthLog(Long id) {
        HealthLog healthLog = healthLogRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "보건일지를 찾을 수 없습니다."));

        if (healthLog.getMedicines() != null && !healthLog.getMedicines().isEmpty()) {
            for (HealthLogMedicine oldMed : healthLog.getMedicines()) {
                recordStockIn(
                        LocalDate.now(),
                        oldMed.getMedicineCode(),
                        oldMed.getMedicineName(),
                        oldMed.getQuantity(),
                        healthLog.getId(),
                        "보건일지 삭제에 따른 재고 원복 (" + healthLog.getStudentName() + ")"
                );
            }
        }

        healthLogRepository.delete(healthLog);
    }

    @Transactional
    public int changeDate(HealthLogDto.ChangeDateRequest request) {
        List<HealthLog> logs = healthLogRepository.findAllById(request.getIds());
        for (HealthLog log : logs) {
            log.changeVisitDate(request.getTargetDate());
        }
        return logs.size();
    }

    public List<HealthLogDto.Response> getHealthLogsForPrint(LocalDate startDate, LocalDate endDate, Integer grade) {
        return healthLogRepository.searchForPrint(startDate, endDate, grade)
                .stream()
                .map(HealthLogDto.Response::from)
                .toList();
    }

    private int getCurrentStock(String medicineCode) {
        return medicineLedgerRepository.findTopByMedicineCodeOrderByIdDesc(medicineCode)
                .map(MedicineLedger::getStockQuantity)
                .orElse(0);
    }

    private void recordStockOut(LocalDate ledgerDate, String medicineCode, String medicineName,
                                int quantity, Long refId, String remark) {
        int currentStock = getCurrentStock(medicineCode);
        if (currentStock < quantity) {
            throw new CustomException(
                    ErrorCode.INSUFFICIENT_STOCK,
                    String.format("약품 [%s]의 재고가 부족합니다. (현재 재고: %d, 요청 수량: %d)", medicineName, currentStock, quantity)
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
}
