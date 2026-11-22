package com.school.healthroom.domain.setting.dto;

import com.school.healthroom.domain.setting.entity.SystemSetting;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class SystemSettingDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {
        @NotBlank(message = "학교명은 필수입니다.")
        private String schoolName;

        @NotBlank(message = "보건교사명은 필수입니다.")
        private String teacherName;

        @NotNull(message = "보관기간(년)은 필수입니다.")
        @Min(value = 1, message = "보관기간은 1년 이상이어야 합니다.")
        private Integer retentionYears;

        @NotNull(message = "키오스크 활성화 여부는 필수입니다.")
        private Boolean kioskEnabled;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private String schoolName;
        private String teacherName;
        private Integer retentionYears;
        private Boolean kioskEnabled;
        private LocalDateTime updatedAt;

        public static Response from(SystemSetting setting) {
            return Response.builder()
                    .id(setting.getId())
                    .schoolName(setting.getSchoolName())
                    .teacherName(setting.getTeacherName())
                    .retentionYears(setting.getRetentionYears())
                    .kioskEnabled(setting.getKioskEnabled())
                    .updatedAt(setting.getUpdatedAt())
                    .build();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExpiredDataDeleteResponse {
        private LocalDate cutoffDate;
        private int deletedHealthLogs;
        private int deletedHospitalTreatments;
        private int deletedMedicinePurchases;
        private int deletedMedicineLedgers;
        private int totalDeletedCount;
    }
}
