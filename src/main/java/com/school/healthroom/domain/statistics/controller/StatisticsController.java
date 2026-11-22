package com.school.healthroom.domain.statistics.controller;

import com.school.healthroom.domain.statistics.dto.StatisticsDto;
import com.school.healthroom.domain.statistics.service.StatisticsService;
import com.school.healthroom.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/medicine-usage")
    public ResponseEntity<ApiResponse<List<StatisticsDto.MedicineUsageItem>>> getMedicineUsageStatistics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(ApiResponse.ok(
                statisticsService.getMedicineUsageStatistics(startDate, endDate)));
    }

    @GetMapping("/symptoms")
    public ResponseEntity<ApiResponse<List<StatisticsDto.SymptomStatItem>>> getSymptomStatistics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(ApiResponse.ok(
                statisticsService.getSymptomStatistics(startDate, endDate)));
    }

    @GetMapping("/monthly/grade-class")
    public ResponseEntity<ApiResponse<List<StatisticsDto.GradeClassMonthlyItem>>> getMonthlyGradeClassStatistics(
            @RequestParam(required = false) Integer year) {
        int targetYear = year != null ? year : LocalDate.now().getYear();
        return ResponseEntity.ok(ApiResponse.ok(
                statisticsService.getMonthlyGradeClassStatistics(targetYear)));
    }

    @GetMapping("/monthly/symptoms")
    public ResponseEntity<ApiResponse<List<StatisticsDto.SymptomMonthlyItem>>> getMonthlySymptomStatistics(
            @RequestParam(required = false) Integer year) {
        int targetYear = year != null ? year : LocalDate.now().getYear();
        return ResponseEntity.ok(ApiResponse.ok(
                statisticsService.getMonthlySymptomStatistics(targetYear)));
    }
}
