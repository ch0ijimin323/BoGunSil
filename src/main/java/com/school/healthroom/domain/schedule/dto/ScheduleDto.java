package com.school.healthroom.domain.schedule.dto;

import com.school.healthroom.domain.schedule.entity.Schedule;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class ScheduleDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SaveRequest {
        @NotBlank(message = "일정 제목은 필수입니다.")
        private String title;

        @NotNull(message = "시작일은 필수입니다.")
        private LocalDate startDate;

        @NotNull(message = "종료일은 필수입니다.")
        private LocalDate endDate;

        private LocalTime startTime;
        private LocalTime endTime;
        private String category;
        private String content;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private String title;
        private LocalDate startDate;
        private LocalDate endDate;
        private LocalTime startTime;
        private LocalTime endTime;
        private String category;
        private String content;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static Response from(Schedule entity) {
            return Response.builder()
                    .id(entity.getId())
                    .title(entity.getTitle())
                    .startDate(entity.getStartDate())
                    .endDate(entity.getEndDate())
                    .startTime(entity.getStartTime())
                    .endTime(entity.getEndTime())
                    .category(entity.getCategory())
                    .content(entity.getContent())
                    .createdAt(entity.getCreatedAt())
                    .updatedAt(entity.getUpdatedAt())
                    .build();
        }
    }
}
