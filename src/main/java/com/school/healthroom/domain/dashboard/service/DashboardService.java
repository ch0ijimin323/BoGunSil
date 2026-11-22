package com.school.healthroom.domain.dashboard.service;

import com.school.healthroom.domain.carestudent.repository.CareStudentRepository;
import com.school.healthroom.domain.dashboard.dto.DashboardResponse;
import com.school.healthroom.domain.healthlog.repository.HealthLogRepository;
import com.school.healthroom.domain.schedule.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final HealthLogRepository healthLogRepository;
    private final CareStudentRepository careStudentRepository;
    private final ScheduleRepository scheduleRepository;

    public DashboardResponse getDashboard() {
        LocalDate today = LocalDate.now();
        YearMonth currentMonth = YearMonth.from(today);
        LocalDate monthStart = currentMonth.atDay(1);
        LocalDate monthEnd = currentMonth.atEndOfMonth();

        long todayVisitorCount = healthLogRepository.countByVisitDate(today);
        long monthVisitorCount = healthLogRepository.countByVisitDateBetween(monthStart, monthEnd);
        long careStudentCount = careStudentRepository.count();

        List<DashboardResponse.TodayScheduleItem> todaySchedules = scheduleRepository.findTodaySchedules(today)
                .stream()
                .map(DashboardResponse.TodayScheduleItem::from)
                .toList();

        List<DashboardResponse.RecentHealthLogItem> recentHealthLogs = healthLogRepository
                .findTop5ByOrderByVisitDateDescVisitTimeDescIdDesc()
                .stream()
                .map(DashboardResponse.RecentHealthLogItem::from)
                .toList();

        return DashboardResponse.builder()
                .todayVisitorCount(todayVisitorCount)
                .monthVisitorCount(monthVisitorCount)
                .careStudentCount(careStudentCount)
                .todaySchedules(todaySchedules)
                .recentHealthLogs(recentHealthLogs)
                .build();
    }
}
