package com.school.healthroom.domain.carestudent.dto;

import com.school.healthroom.domain.carestudent.entity.CareStudent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class CareStudentDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SaveRequest {
        @NotNull(message = "학년도는 필수입니다.")
        private Integer schoolYear;

        @NotNull(message = "학년은 필수입니다.")
        @Min(value = 1, message = "학년은 1 이상이어야 합니다.")
        private Integer grade;

        @NotNull(message = "반은 필수입니다.")
        @Min(value = 1, message = "반은 1 이상이어야 합니다.")
        private Integer classNo;

        @NotNull(message = "번호는 필수입니다.")
        @Min(value = 1, message = "번호는 1 이상이어야 합니다.")
        private Integer studentNo;

        @NotBlank(message = "학생 이름은 필수입니다.")
        private String studentName;

        @NotBlank(message = "질환명은 필수입니다.")
        private String diseaseName;

        private String emergencyContact;
        private String careGuide;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private Integer schoolYear;
        private Integer grade;
        private Integer classNo;
        private Integer studentNo;
        private String studentName;
        private String diseaseName;
        private String emergencyContact;
        private String careGuide;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static Response from(CareStudent entity) {
            return Response.builder()
                    .id(entity.getId())
                    .schoolYear(entity.getSchoolYear())
                    .grade(entity.getGrade())
                    .classNo(entity.getClassNo())
                    .studentNo(entity.getStudentNo())
                    .studentName(entity.getStudentName())
                    .diseaseName(entity.getDiseaseName())
                    .emergencyContact(entity.getEmergencyContact())
                    .careGuide(entity.getCareGuide())
                    .createdAt(entity.getCreatedAt())
                    .updatedAt(entity.getUpdatedAt())
                    .build();
        }
    }
}
