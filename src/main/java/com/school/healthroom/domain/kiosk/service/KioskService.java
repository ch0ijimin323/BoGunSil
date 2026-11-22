package com.school.healthroom.domain.kiosk.service;

import com.school.healthroom.domain.commoncode.repository.CommonCodeRepository;
import com.school.healthroom.domain.gradeclass.dto.GradeClassDto;
import com.school.healthroom.domain.gradeclass.entity.GradeClass;
import com.school.healthroom.domain.gradeclass.repository.GradeClassRepository;
import com.school.healthroom.domain.healthlog.dto.HealthLogDto;
import com.school.healthroom.domain.healthlog.entity.HealthLog;
import com.school.healthroom.domain.healthlog.repository.HealthLogRepository;
import com.school.healthroom.domain.kiosk.dto.KioskDto;
import com.school.healthroom.domain.setting.entity.SystemSetting;
import com.school.healthroom.domain.setting.service.SystemSettingService;
import com.school.healthroom.global.error.CustomException;
import com.school.healthroom.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KioskService {

    private final GradeClassRepository gradeClassRepository;
    private final CommonCodeRepository commonCodeRepository;
    private final HealthLogRepository healthLogRepository;
    private final SystemSettingService systemSettingService;

    public List<GradeClassDto.Response> getCurrentYearGradeClasses() {
        int currentYear = LocalDate.now().getYear();
        List<GradeClass> classes = gradeClassRepository.findBySchoolYearOrderByGradeAscClassNoAsc(currentYear);
        if (classes.isEmpty()) {
            classes = gradeClassRepository.search(null, null);
        }
        return classes.stream()
                .map(GradeClassDto.Response::from)
                .toList();
    }

    public List<KioskDto.CodeOptionResponse> getSymptoms() {
        return commonCodeRepository.findByGroupCodeAndUseYnTrueOrderBySortOrderAscIdAsc("SYMPTOM")
                .stream()
                .map(c -> KioskDto.CodeOptionResponse.builder()
                        .code(c.getCode())
                        .codeName(c.getCodeName())
                        .parentCode(c.getParentCode())
                        .description(c.getDescription())
                        .build())
                .toList();
    }

    public List<KioskDto.CodeOptionResponse> getTreatmentsBySymptom(String symptomCode) {
        var list = (symptomCode == null || symptomCode.isBlank())
                ? commonCodeRepository.findByGroupCodeAndUseYnTrueOrderBySortOrderAscIdAsc("TREATMENT")
                : commonCodeRepository.findByGroupCodeAndParentCodeAndUseYnTrueOrderBySortOrderAscIdAsc("TREATMENT", symptomCode);

        return list.stream()
                .map(c -> KioskDto.CodeOptionResponse.builder()
                        .code(c.getCode())
                        .codeName(c.getCodeName())
                        .parentCode(c.getParentCode())
                        .description(c.getDescription())
                        .build())
                .toList();
    }

    @Transactional
    public HealthLogDto.Response createVisit(KioskDto.VisitRequest request) {
        SystemSetting setting = systemSettingService.getOrCreateSettingEntity();
        if (!Boolean.TRUE.equals(setting.getKioskEnabled())) {
            throw new CustomException(ErrorCode.INVALID_INPUT, "현재 학생 키오스크 방문 접수가 비활성화되어 있습니다.");
        }

        HealthLog healthLog = HealthLog.builder()
                .visitDate(LocalDate.now())
                .visitTime(LocalTime.now())
                .grade(request.getGrade())
                .classNo(request.getClassNo())
                .studentNo(request.getStudentNo())
                .studentName(request.getStudentName())
                .gender(request.getGender())
                .symptomCode(request.getSymptomCode())
                .symptomDetail(request.getSymptomDetail())
                .treatmentCode(request.getTreatmentCode())
                .treatmentDetail(request.getTreatmentDetail())
                .sourceType("KIOSK")
                .memo(request.getMemo())
                .build();

        return HealthLogDto.Response.from(healthLogRepository.save(healthLog));
    }
}
