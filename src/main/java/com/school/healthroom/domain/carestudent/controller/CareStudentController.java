package com.school.healthroom.domain.carestudent.controller;

import com.school.healthroom.domain.carestudent.dto.CareStudentDto;
import com.school.healthroom.domain.carestudent.service.CareStudentService;
import com.school.healthroom.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/care-students")
@RequiredArgsConstructor
public class CareStudentController {

    private final CareStudentService careStudentService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CareStudentDto.Response>>> getCareStudents(
            @RequestParam(required = false) Integer schoolYear,
            @RequestParam(required = false) Integer grade,
            @RequestParam(required = false) Integer classNo,
            @RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(ApiResponse.ok(careStudentService.getCareStudents(schoolYear, grade, classNo, keyword)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CareStudentDto.Response>> getCareStudent(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(careStudentService.getCareStudent(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CareStudentDto.Response>> createCareStudent(
            @Valid @RequestBody CareStudentDto.SaveRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(careStudentService.createCareStudent(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CareStudentDto.Response>> updateCareStudent(
            @PathVariable Long id,
            @Valid @RequestBody CareStudentDto.SaveRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(careStudentService.updateCareStudent(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCareStudent(@PathVariable Long id) {
        careStudentService.deleteCareStudent(id);
        return ResponseEntity.ok(ApiResponse.okMessage("요양호자 정보가 삭제되었습니다."));
    }
}
