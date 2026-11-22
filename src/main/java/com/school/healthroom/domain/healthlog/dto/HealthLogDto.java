package com.school.healthroom.domain.healthlog.dto;

import com.school.healthroom.domain.healthlog.entity.HealthLog;
import com.school.healthroom.domain.healthlog.entity.HealthLogMedicine;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

public class HealthLogDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MedicineItemRequest {
        @NotBlank(message = "약품코드는 필수입니다.")
        private String medicineCode;

        @NotBlank(message = "약품명은 필수입니다.")
        private String medicineName;

        @NotNull(message = "투약 수량은 필수입니다.")
        @Min(value = 1, message = "투약 수량은 1 이상이어야 합니다.")
        private Integer quantity;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SaveRequest {
        @NotNull(message = "방문일자는 필수입니다.")
        private LocalDate visitDate;

        private LocalTime visitTime;

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

        @Valid
        private List<MedicineItemRequest> medicines;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChangeDateRequest {
        @NotEmpty(message = "일자를 변경할 보건일지 ID 목록은 필수입니다.")
        private List<Long> ids;

        @NotNull(message = "변경할 방문일자는 필수입니다.")
        private LocalDate targetDate;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MedicineItemResponse {
        private Long id;
        private String medicineCode;
        private String medicineName;
        private Integer quantity;

        public static MedicineItemResponse from(HealthLogMedicine entity) {
            return MedicineItemResponse.builder()
                    .id(entity.getId())
                    .medicineCode(entity.getMedicineCode())
                    .medicineName(entity.getMedicineName())
                    .quantity(entity.getQuantity())
                    .build();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private LocalDate visitDate;
        private LocalTime visitTime;
        private Integer grade;
        private Integer classNo;
        private Integer studentNo;
        private String studentName;
        private String gender;
        private String symptomCode;
        private String symptomDetail;
        private String treatmentCode;
        private String treatmentDetail;
        private String sourceType;
        private String memo;
        private List<MedicineItemResponse> medicines;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static Response from(HealthLog entity) {
            List<MedicineItemResponse> medList = entity.getMedicines() != null
                    ? entity.getMedicines().stream().map(MedicineItemResponse::from).toList()
                    : Collections.emptyList();

            return Response.builder()
                    .id(entity.getId())
                    .visitDate(entity.getVisitDate())
                    .visitTime(entity.getVisitTime())
                    .grade(entity.getGrade())
                    .classNo(entity.getClassNo())
                    .studentNo(entity.getStudentNo())
                    .studentName(entity.getStudentName())
                    .gender(entity.getGender())
                    .symptomCode(entity.getSymptomCode())
                    .symptomDetail(entity.getSymptomDetail())
                    .treatmentCode(entity.getTreatmentCode())
                    .treatmentDetail(entity.getTreatmentDetail())
                    .sourceType(entity.getSourceType())
                    .memo(entity.getMemo())
                    .medicines(medList)
                    .createdAt(entity.getCreatedAt())
                    .updatedAt(entity.getUpdatedAt())
                    .build();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PageResponse {
        private List<Response> content;
        private int page;
        private int size;
        private long totalElements;
        private int totalPages;
        private boolean last;

        public static PageResponse from(Page<HealthLog> pageResult) {
            return PageResponse.builder()
                    .content(pageResult.getContent().stream().map(Response::from).toList())
                    .page(pageResult.getNumber())
                    .size(pageResult.getSize())
                    .totalElements(pageResult.getTotalElements())
                    .totalPages(pageResult.getTotalPages())
                    .last(pageResult.isLast())
                    .build();
        }
    }
}
