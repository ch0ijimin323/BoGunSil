package com.school.healthroom.domain.schedule.controller;

import com.school.healthroom.domain.schedule.dto.ScheduleDto;
import com.school.healthroom.domain.schedule.service.ScheduleService;
import com.school.healthroom.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ScheduleDto.Response>>> getSchedules(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(ApiResponse.ok(scheduleService.getSchedules(startDate, endDate, keyword)));
    }

    @GetMapping("/calendar")
    public ResponseEntity<ApiResponse<List<ScheduleDto.Response>>> getCalendarSchedules(
            @RequestParam int year,
            @RequestParam int month) {
        return ResponseEntity.ok(ApiResponse.ok(scheduleService.getCalendarSchedules(year, month)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ScheduleDto.Response>> createSchedule(
            @Valid @RequestBody ScheduleDto.SaveRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(scheduleService.createSchedule(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ScheduleDto.Response>> updateSchedule(
            @PathVariable Long id,
            @Valid @RequestBody ScheduleDto.SaveRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(scheduleService.updateSchedule(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSchedule(@PathVariable Long id) {
        scheduleService.deleteSchedule(id);
        return ResponseEntity.ok(ApiResponse.okMessage("일정이 삭제되었습니다."));
    }
}
