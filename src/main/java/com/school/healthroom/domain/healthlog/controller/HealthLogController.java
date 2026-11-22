package com.school.healthroom.domain.healthlog.controller;

import com.school.healthroom.domain.healthlog.dto.HealthLogDto;
import com.school.healthroom.domain.healthlog.service.HealthLogService;
import com.school.healthroom.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/health-logs")
@RequiredArgsConstructor
public class HealthLogController {

    private final HealthLogService healthLogService;

    @GetMapping
    public ResponseEntity<ApiResponse<HealthLogDto.PageResponse>> getHealthLogs(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Integer grade,
            @RequestParam(required = false) Integer classNo,
            @RequestParam(required = false) String symptomCode,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.ok(
                healthLogService.getHealthLogs(startDate, endDate, grade, classNo, symptomCode, keyword, page, size)));
    }

    @GetMapping("/print")
    public ResponseEntity<ApiResponse<List<HealthLogDto.Response>>> getHealthLogsForPrint(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Integer grade) {
        return ResponseEntity.ok(ApiResponse.ok(
                healthLogService.getHealthLogsForPrint(startDate, endDate, grade)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<HealthLogDto.Response>> getHealthLog(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(healthLogService.getHealthLog(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<HealthLogDto.Response>> createHealthLog(
            @Valid @RequestBody HealthLogDto.SaveRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(healthLogService.createHealthLog(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<HealthLogDto.Response>> updateHealthLog(
            @PathVariable Long id,
            @Valid @RequestBody HealthLogDto.SaveRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(healthLogService.updateHealthLog(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteHealthLog(@PathVariable Long id) {
        healthLogService.deleteHealthLog(id);
        return ResponseEntity.ok(ApiResponse.okMessage("보건일지가 삭제되었습니다."));
    }

    @PatchMapping("/change-date")
    public ResponseEntity<ApiResponse<Map<String, Object>>> changeDate(
            @Valid @RequestBody HealthLogDto.ChangeDateRequest request) {
        int updatedCount = healthLogService.changeDate(request);
        return ResponseEntity.ok(ApiResponse.ok(Map.of(
                "updatedCount", updatedCount,
                "targetDate", request.getTargetDate()
        )));
    }
}
