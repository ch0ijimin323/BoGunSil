package com.school.healthroom.domain.kiosk.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class KioskDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VisitRequest {
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

        private String gender;

        @NotBlank(message = "증상코드는 필수입니다.")
        private String symptomCode;

        private String symptomDetail;
        private String treatmentCode;
        private String treatmentDetail;
        private String memo;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CodeOptionResponse {
        private String code;
        private String codeName;
        private String parentCode;
        private String description;
    }
}
