package com.school.healthroom.domain.setting.controller;

import com.school.healthroom.domain.setting.dto.SystemSettingDto;
import com.school.healthroom.domain.setting.service.SystemSettingService;
import com.school.healthroom.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/settings")
@RequiredArgsConstructor
public class SystemSettingController {

    private final SystemSettingService systemSettingService;

    @GetMapping
    public ResponseEntity<ApiResponse<SystemSettingDto.Response>> getSettings() {
        return ResponseEntity.ok(ApiResponse.ok(systemSettingService.getSettings()));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<SystemSettingDto.Response>> updateSettings(
            @Valid @RequestBody SystemSettingDto.UpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(systemSettingService.updateSettings(request)));
    }

    @PostMapping("/reset")
    public ResponseEntity<ApiResponse<SystemSettingDto.Response>> resetSettings() {
        return ResponseEntity.ok(ApiResponse.ok(systemSettingService.resetSettings()));
    }

    @DeleteMapping("/expired-data")
    public ResponseEntity<ApiResponse<SystemSettingDto.ExpiredDataDeleteResponse>> deleteExpiredData() {
        return ResponseEntity.ok(ApiResponse.ok(systemSettingService.deleteExpiredData()));
    }
}
