package com.school.healthroom.domain.kiosk.controller;

import com.school.healthroom.domain.gradeclass.dto.GradeClassDto;
import com.school.healthroom.domain.healthlog.dto.HealthLogDto;
import com.school.healthroom.domain.kiosk.dto.KioskDto;
import com.school.healthroom.domain.kiosk.service.KioskService;
import com.school.healthroom.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class KioskController {

    private final KioskService kioskService;

    @GetMapping("/grade-classes")
    public ResponseEntity<ApiResponse<List<GradeClassDto.Response>>> getGradeClasses() {
        return ResponseEntity.ok(ApiResponse.ok(kioskService.getCurrentYearGradeClasses()));
    }

    @GetMapping("/symptoms")
    public ResponseEntity<ApiResponse<List<KioskDto.CodeOptionResponse>>> getSymptoms() {
        return ResponseEntity.ok(ApiResponse.ok(kioskService.getSymptoms()));
    }

    @GetMapping("/treatments")
    public ResponseEntity<ApiResponse<List<KioskDto.CodeOptionResponse>>> getTreatments(
            @RequestParam(required = false) String symptomCode) {
        return ResponseEntity.ok(ApiResponse.ok(kioskService.getTreatmentsBySymptom(symptomCode)));
    }

    @PostMapping("/visit")
    public ResponseEntity<ApiResponse<HealthLogDto.Response>> createVisit(
            @Valid @RequestBody KioskDto.VisitRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(kioskService.createVisit(request)));
    }
}
