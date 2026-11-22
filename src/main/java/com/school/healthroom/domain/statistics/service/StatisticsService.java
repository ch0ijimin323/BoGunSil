package com.school.healthroom.domain.statistics.service;

import com.school.healthroom.domain.commoncode.entity.CommonCode;
import com.school.healthroom.domain.commoncode.repository.CommonCodeRepository;
import com.school.healthroom.domain.gradeclass.entity.GradeClass;
import com.school.healthroom.domain.gradeclass.repository.GradeClassRepository;
import com.school.healthroom.domain.healthlog.entity.HealthLog;
import com.school.healthroom.domain.healthlog.entity.HealthLogMedicine;
import com.school.healthroom.domain.healthlog.repository.HealthLogMedicineRepository;
import com.school.healthroom.domain.healthlog.repository.HealthLogRepository;
import com.school.healthroom.domain.statistics.dto.StatisticsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticsService {

    private final HealthLogRepository healthLogRepository;
    private final HealthLogMedicineRepository healthLogMedicineRepository;
    private final CommonCodeRepository commonCodeRepository;
    private final GradeClassRepository gradeClassRepository;

    public List<StatisticsDto.MedicineUsageItem> getMedicineUsageStatistics(LocalDate startDate, LocalDate endDate) {
        List<HealthLogMedicine> usages = healthLogMedicineRepository.findByPeriod(startDate, endDate);

        Map<String, List<HealthLogMedicine>> grouped = usages.stream()
                .collect(Collectors.groupingBy(HealthLogMedicine::getMedicineCode, LinkedHashMap::new, Collectors.toList()));

        List<StatisticsDto.MedicineUsageItem> result = new ArrayList<>();
        for (Map.Entry<String, List<HealthLogMedicine>> entry : grouped.entrySet()) {
            String code = entry.getKey();
            List<HealthLogMedicine> items = entry.getValue();
            String name = items.get(0).getMedicineName();
            long usageCount = items.size();
            long totalQty = items.stream().mapToLong(HealthLogMedicine::getQuantity).sum();

            result.add(StatisticsDto.MedicineUsageItem.builder()
                    .medicineCode(code)
                    .medicineName(name)
                    .usageCount(usageCount)
                    .totalQuantity(totalQty)
                    .build());
        }

        result.sort(Comparator.comparingLong(StatisticsDto.MedicineUsageItem::getTotalQuantity).reversed());
        return result;
    }

    public List<StatisticsDto.SymptomStatItem> getSymptomStatistics(LocalDate startDate, LocalDate endDate) {
        List<HealthLog> logs = healthLogRepository.findByPeriod(startDate, endDate);
        long totalVisits = logs.size();

        Map<String, String> symptomCodeNameMap = commonCodeRepository.search("SYMPTOM", null)
                .stream()
                .collect(Collectors.toMap(CommonCode::getCode, CommonCode::getCodeName, (a, b) -> a));

        Map<String, Long> counts = logs.stream()
                .collect(Collectors.groupingBy(HealthLog::getSymptomCode, Collectors.counting()));

        List<StatisticsDto.SymptomStatItem> result = new ArrayList<>();
        for (Map.Entry<String, Long> entry : counts.entrySet()) {
            String code = entry.getKey();
            long count = entry.getValue();
            double percentage = totalVisits > 0
                    ? Math.round((count * 10000.0) / totalVisits) / 100.0
                    : 0.0;

            result.add(StatisticsDto.SymptomStatItem.builder()
                    .symptomCode(code)
                    .symptomName(symptomCodeNameMap.getOrDefault(code, code))
                    .visitCount(count)
                    .percentage(percentage)
                    .build());
        }

        result.sort(Comparator.comparingLong(StatisticsDto.SymptomStatItem::getVisitCount).reversed());
        return result;
    }

    public List<StatisticsDto.GradeClassMonthlyItem> getMonthlyGradeClassStatistics(int year) {
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);

        List<HealthLog> logs = healthLogRepository.findByPeriod(startDate, endDate);
        List<GradeClass> gradeClasses = gradeClassRepository.findBySchoolYearOrderByGradeAscClassNoAsc(year);

        Map<String, Map<Integer, Long>> matrix = new LinkedHashMap<>();
        for (GradeClass gc : gradeClasses) {
            String key = gc.getGrade() + "-" + gc.getClassNo();
            matrix.putIfAbsent(key, createEmptyMonthMap());
        }

        for (HealthLog log : logs) {
            String key = log.getGrade() + "-" + log.getClassNo();
            Map<Integer, Long> monthMap = matrix.computeIfAbsent(key, k -> createEmptyMonthMap());
            int month = log.getVisitDate().getMonthValue();
            monthMap.put(month, monthMap.getOrDefault(month, 0L) + 1L);
        }

        List<StatisticsDto.GradeClassMonthlyItem> result = new ArrayList<>();
        for (Map.Entry<String, Map<Integer, Long>> entry : matrix.entrySet()) {
            String[] parts = entry.getKey().split("-");
            int grade = Integer.parseInt(parts[0]);
            int classNo = Integer.parseInt(parts[1]);
            Map<Integer, Long> monthlyCounts = entry.getValue();
            long total = monthlyCounts.values().stream().mapToLong(Long::longValue).sum();

            result.add(StatisticsDto.GradeClassMonthlyItem.builder()
                    .grade(grade)
                    .classNo(classNo)
                    .monthlyCounts(monthlyCounts)
                    .totalCount(total)
                    .build());
        }

        result.sort(Comparator.comparing(StatisticsDto.GradeClassMonthlyItem::getGrade)
                .thenComparing(StatisticsDto.GradeClassMonthlyItem::getClassNo));
        return result;
    }

    public List<StatisticsDto.SymptomMonthlyItem> getMonthlySymptomStatistics(int year) {
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);

        List<HealthLog> logs = healthLogRepository.findByPeriod(startDate, endDate);
        List<CommonCode> symptoms = commonCodeRepository.search("SYMPTOM", null);

        Map<String, String> symptomCodeNameMap = new LinkedHashMap<>();
        Map<String, Map<Integer, Long>> matrix = new LinkedHashMap<>();

        for (CommonCode code : symptoms) {
            symptomCodeNameMap.put(code.getCode(), code.getCodeName());
            matrix.put(code.getCode(), createEmptyMonthMap());
        }

        for (HealthLog log : logs) {
            String code = log.getSymptomCode();
            symptomCodeNameMap.putIfAbsent(code, code);
            Map<Integer, Long> monthMap = matrix.computeIfAbsent(code, k -> createEmptyMonthMap());
            int month = log.getVisitDate().getMonthValue();
            monthMap.put(month, monthMap.getOrDefault(month, 0L) + 1L);
        }

        List<StatisticsDto.SymptomMonthlyItem> result = new ArrayList<>();
        for (Map.Entry<String, Map<Integer, Long>> entry : matrix.entrySet()) {
            String code = entry.getKey();
            Map<Integer, Long> monthlyCounts = entry.getValue();
            long total = monthlyCounts.values().stream().mapToLong(Long::longValue).sum();

            result.add(StatisticsDto.SymptomMonthlyItem.builder()
                    .symptomCode(code)
                    .symptomName(symptomCodeNameMap.getOrDefault(code, code))
                    .monthlyCounts(monthlyCounts)
                    .totalCount(total)
                    .build());
        }

        return result;
    }

    private Map<Integer, Long> createEmptyMonthMap() {
        Map<Integer, Long> map = new LinkedHashMap<>();
        for (int m = 1; m <= 12; m++) {
            map.put(m, 0L);
        }
        return map;
    }
}
