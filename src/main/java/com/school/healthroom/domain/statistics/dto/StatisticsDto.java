package com.school.healthroom.domain.statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

public class StatisticsDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MedicineUsageItem {
        private String medicineCode;
        private String medicineName;
        private long usageCount;
        private long totalQuantity;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SymptomStatItem {
        private String symptomCode;
        private String symptomName;
        private long visitCount;
        private double percentage;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GradeClassMonthlyItem {
        private Integer grade;
        private Integer classNo;
        private Map<Integer, Long> monthlyCounts; // 1 ~ 12
        private long totalCount;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SymptomMonthlyItem {
        private String symptomCode;
        private String symptomName;
        private Map<Integer, Long> monthlyCounts; // 1 ~ 12
        private long totalCount;
    }
}
