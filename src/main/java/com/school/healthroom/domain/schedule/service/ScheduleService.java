package com.school.healthroom.domain.schedule.service;

import com.school.healthroom.domain.schedule.dto.ScheduleDto;
import com.school.healthroom.domain.schedule.entity.Schedule;
import com.school.healthroom.domain.schedule.repository.ScheduleRepository;
import com.school.healthroom.global.error.CustomException;
import com.school.healthroom.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    public List<ScheduleDto.Response> getSchedules(LocalDate startDate, LocalDate endDate, String keyword) {
        return scheduleRepository.search(startDate, endDate, keyword)
                .stream()
                .map(ScheduleDto.Response::from)
                .toList();
    }

    public List<ScheduleDto.Response> getCalendarSchedules(int year, int month) {
        if (month < 1 || month > 12) {
            throw new CustomException(ErrorCode.INVALID_INPUT, "월(month)은 1~12 사이여야 합니다.");
        }
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate monthStart = yearMonth.atDay(1);
        LocalDate monthEnd = yearMonth.atEndOfMonth();

        return scheduleRepository.findOverlappingPeriod(monthStart, monthEnd)
                .stream()
                .map(ScheduleDto.Response::from)
                .toList();
    }

    @Transactional
    public ScheduleDto.Response createSchedule(ScheduleDto.SaveRequest request) {
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new CustomException(ErrorCode.INVALID_INPUT, "종료일은 시작일보다 빠를 수 없습니다.");
        }

        Schedule schedule = Schedule.builder()
                .title(request.getTitle())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .category(request.getCategory())
                .content(request.getContent())
                .build();

        return ScheduleDto.Response.from(scheduleRepository.save(schedule));
    }

    @Transactional
    public ScheduleDto.Response updateSchedule(Long id, ScheduleDto.SaveRequest request) {
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new CustomException(ErrorCode.INVALID_INPUT, "종료일은 시작일보다 빠를 수 없습니다.");
        }

        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "일정을 찾을 수 없습니다."));

        schedule.update(
                request.getTitle(),
                request.getStartDate(),
                request.getEndDate(),
                request.getStartTime(),
                request.getEndTime(),
                request.getCategory(),
                request.getContent()
        );
        return ScheduleDto.Response.from(schedule);
    }

    @Transactional
    public void deleteSchedule(Long id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "일정을 찾을 수 없습니다."));
        scheduleRepository.delete(schedule);
    }
}
