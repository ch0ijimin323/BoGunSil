package com.school.healthroom.domain.setting.service;

import com.school.healthroom.domain.healthlog.entity.HealthLog;
import com.school.healthroom.domain.healthlog.repository.HealthLogRepository;
import com.school.healthroom.domain.hospital.repository.HospitalTreatmentRepository;
import com.school.healthroom.domain.medicine.repository.MedicineLedgerRepository;
import com.school.healthroom.domain.medicine.repository.MedicinePurchaseRepository;
import com.school.healthroom.domain.setting.dto.SystemSettingDto;
import com.school.healthroom.domain.setting.entity.SystemSetting;
import com.school.healthroom.domain.setting.repository.SystemSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SystemSettingService {

    private final SystemSettingRepository systemSettingRepository;
    private final HealthLogRepository healthLogRepository;
    private final HospitalTreatmentRepository hospitalTreatmentRepository;
    private final MedicinePurchaseRepository medicinePurchaseRepository;
    private final MedicineLedgerRepository medicineLedgerRepository;

    @Transactional
    public SystemSetting getOrCreateSettingEntity() {
        return systemSettingRepository.findById(SystemSetting.DEFAULT_ID)
                .orElseGet(() -> systemSettingRepository.save(SystemSetting.createDefault()));
    }

    @Transactional
    public SystemSettingDto.Response getSettings() {
        return SystemSettingDto.Response.from(getOrCreateSettingEntity());
    }

    @Transactional
    public SystemSettingDto.Response updateSettings(SystemSettingDto.UpdateRequest request) {
        SystemSetting setting = getOrCreateSettingEntity();
        setting.update(
                request.getSchoolName(),
                request.getTeacherName(),
                request.getRetentionYears(),
                request.getKioskEnabled()
        );
        return SystemSettingDto.Response.from(setting);
    }

    @Transactional
    public SystemSettingDto.Response resetSettings() {
        SystemSetting setting = getOrCreateSettingEntity();
        setting.resetToDefault();
        return SystemSettingDto.Response.from(setting);
    }

    @Transactional
    public SystemSettingDto.ExpiredDataDeleteResponse deleteExpiredData() {
        SystemSetting setting = getOrCreateSettingEntity();
        int retentionYears = setting.getRetentionYears() != null ? setting.getRetentionYears() : 5;
        LocalDate cutoffDate = LocalDate.now().minusYears(retentionYears);

        List<HealthLog> expiredLogs = healthLogRepository.findByVisitDateBefore(cutoffDate);
        int deletedHealthLogs = expiredLogs.size();
        if (!expiredLogs.isEmpty()) {
            healthLogRepository.deleteAll(expiredLogs);
        }

        int deletedHospitalTreatments = hospitalTreatmentRepository.deleteByVisitDateBefore(cutoffDate);
        int deletedMedicinePurchases = medicinePurchaseRepository.deleteByPurchaseDateBefore(cutoffDate);
        int deletedMedicineLedgers = medicineLedgerRepository.deleteByLedgerDateBefore(cutoffDate);

        int totalDeleted = deletedHealthLogs + deletedHospitalTreatments + deletedMedicinePurchases + deletedMedicineLedgers;

        return SystemSettingDto.ExpiredDataDeleteResponse.builder()
                .cutoffDate(cutoffDate)
                .deletedHealthLogs(deletedHealthLogs)
                .deletedHospitalTreatments(deletedHospitalTreatments)
                .deletedMedicinePurchases(deletedMedicinePurchases)
                .deletedMedicineLedgers(deletedMedicineLedgers)
                .totalDeletedCount(totalDeleted)
                .build();
    }
}
