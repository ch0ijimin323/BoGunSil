package com.school.healthroom.domain.dashboard.dto;

import com.school.healthroom.domain.healthlog.entity.HealthLog;
import com.school.healthroom.domain.schedule.entity.Schedule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {

    private long todayVisitorCount;
    private long monthVisitorCount;
    private long careStudentCount;
    private List<TodayScheduleItem> todaySchedules;
    private List<RecentHealthLogItem> recentHealthLogs;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TodayScheduleItem {
        private Long id;
        private String title;
        private LocalDate startDate;
        private LocalDate endDate;
        private LocalTime startTime;
        private LocalTime endTime;
        private String category;
        private String content;

        public static TodayScheduleItem from(Schedule schedule) {
            return TodayScheduleItem.builder()
                    .id(schedule.getId())
                    .title(schedule.getTitle())
                    .startDate(schedule.getStartDate())
                    .endDate(schedule.getEndDate())
                    .startTime(schedule.getStartTime())
                    .endTime(schedule.getEndTime())
                    .category(schedule.getCategory())
                    .content(schedule.getContent())
                    .build();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentHealthLogItem {
        private Long id;
        private LocalDate visitDate;
        private LocalTime visitTime;
        private Integer grade;
        private Integer classNo;
        private Integer studentNo;
        private String studentName;
        private String symptomCode;
        private String symptomDetail;
        private String treatmentCode;
        private String treatmentDetail;
        private String sourceType;

        public static RecentHealthLogItem from(HealthLog healthLog) {
            return RecentHealthLogItem.builder()
                    .id(healthLog.getId())
                    .visitDate(healthLog.getVisitDate())
                    .visitTime(healthLog.getVisitTime())
                    .grade(healthLog.getGrade())
                    .classNo(healthLog.getClassNo())
                    .studentNo(healthLog.getStudentNo())
                    .studentName(healthLog.getStudentName())
                    .symptomCode(healthLog.getSymptomCode())
                    .symptomDetail(healthLog.getSymptomDetail())
                    .treatmentCode(healthLog.getTreatmentCode())
                    .treatmentDetail(healthLog.getTreatmentDetail())
                    .sourceType(healthLog.getSourceType())
                    .build();
        }
    }
}
