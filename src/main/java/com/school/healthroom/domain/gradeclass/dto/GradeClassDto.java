package com.school.healthroom.domain.gradeclass.dto;

import com.school.healthroom.domain.gradeclass.entity.GradeClass;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class GradeClassDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        @NotNull(message = "학년도는 필수입니다.")
        private Integer schoolYear;

        @NotNull(message = "학년은 필수입니다.")
        @Min(value = 1, message = "학년은 1 이상이어야 합니다.")
        private Integer grade;

        @NotNull(message = "반은 필수입니다.")
        @Min(value = 1, message = "반은 1 이상이어야 합니다.")
        private Integer classNo;

        private String teacherName;

        private Integer studentCount;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {
        @NotNull(message = "학년은 필수입니다.")
        @Min(value = 1, message = "학년은 1 이상이어야 합니다.")
        private Integer grade;

        @NotNull(message = "반은 필수입니다.")
        @Min(value = 1, message = "반은 1 이상이어야 합니다.")
        private Integer classNo;

        private String teacherName;

        private Integer studentCount;
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
        private String teacherName;
        private Integer studentCount;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static Response from(GradeClass entity) {
            return Response.builder()
                    .id(entity.getId())
                    .schoolYear(entity.getSchoolYear())
                    .grade(entity.getGrade())
                    .classNo(entity.getClassNo())
                    .teacherName(entity.getTeacherName())
                    .studentCount(entity.getStudentCount())
                    .createdAt(entity.getCreatedAt())
                    .updatedAt(entity.getUpdatedAt())
                    .build();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExcelUploadResponse {
        private int totalProcessed;
        private int insertedCount;
        private int updatedCount;
    }
}
