package com.school.healthroom.domain.hospital.controller;

import com.school.healthroom.domain.hospital.dto.HospitalTreatmentDto;
import com.school.healthroom.domain.hospital.service.HospitalTreatmentService;
import com.school.healthroom.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin/hospital-treatments")
@RequiredArgsConstructor
public class HospitalTreatmentController {

    private final HospitalTreatmentService hospitalTreatmentService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<HospitalTreatmentDto.Response>>> getHospitalTreatments(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Integer grade,
            @RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(ApiResponse.ok(
                hospitalTreatmentService.getHospitalTreatments(startDate, endDate, grade, keyword)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<HospitalTreatmentDto.Response>> getHospitalTreatment(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(hospitalTreatmentService.getHospitalTreatment(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<HospitalTreatmentDto.Response>> createHospitalTreatment(
            @Valid @RequestBody HospitalTreatmentDto.SaveRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(hospitalTreatmentService.createHospitalTreatment(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<HospitalTreatmentDto.Response>> updateHospitalTreatment(
            @PathVariable Long id,
            @Valid @RequestBody HospitalTreatmentDto.SaveRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(hospitalTreatmentService.updateHospitalTreatment(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteHospitalTreatment(@PathVariable Long id) {
        hospitalTreatmentService.deleteHospitalTreatment(id);
        return ResponseEntity.ok(ApiResponse.okMessage("병원치료 내역이 삭제되었습니다."));
    }
}
