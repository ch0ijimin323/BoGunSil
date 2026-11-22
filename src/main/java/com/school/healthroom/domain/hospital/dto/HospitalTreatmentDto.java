package com.school.healthroom.domain.hospital.dto;

import com.school.healthroom.domain.hospital.entity.HospitalTreatment;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class HospitalTreatmentDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SaveRequest {
        @NotNull(message = "방문일자는 필수입니다.")
        private LocalDate visitDate;

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

        @NotBlank(message = "증상은 필수입니다.")
        private String symptom;

        @NotBlank(message = "병원명은 필수입니다.")
        private String hospitalName;

        private String companion;
        private String treatmentResult;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private LocalDate visitDate;
        private Integer grade;
        private Integer classNo;
        private Integer studentNo;
        private String studentName;
        private String symptom;
        private String hospitalName;
        private String companion;
        private String treatmentResult;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static Response from(HospitalTreatment entity) {
            return Response.builder()
                    .id(entity.getId())
                    .visitDate(entity.getVisitDate())
                    .grade(entity.getGrade())
                    .classNo(entity.getClassNo())
                    .studentNo(entity.getStudentNo())
                    .studentName(entity.getStudentName())
                    .symptom(entity.getSymptom())
                    .hospitalName(entity.getHospitalName())
                    .companion(entity.getCompanion())
                    .treatmentResult(entity.getTreatmentResult())
                    .createdAt(entity.getCreatedAt())
                    .updatedAt(entity.getUpdatedAt())
                    .build();
        }
    }
}
