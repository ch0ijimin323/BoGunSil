package com.school.healthroom.domain.gradeclass.controller;

import com.school.healthroom.domain.gradeclass.dto.GradeClassDto;
import com.school.healthroom.domain.gradeclass.service.GradeClassService;
import com.school.healthroom.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/admin/grade-classes")
@RequiredArgsConstructor
public class GradeClassController {

    private final GradeClassService gradeClassService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<GradeClassDto.Response>>> getGradeClasses(
            @RequestParam(required = false) Integer schoolYear,
            @RequestParam(required = false) Integer grade) {
        return ResponseEntity.ok(ApiResponse.ok(gradeClassService.getGradeClasses(schoolYear, grade)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<GradeClassDto.Response>> createGradeClass(
            @Valid @RequestBody GradeClassDto.CreateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(gradeClassService.createGradeClass(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<GradeClassDto.Response>> updateGradeClass(
            @PathVariable Long id,
            @Valid @RequestBody GradeClassDto.UpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(gradeClassService.updateGradeClass(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteGradeClass(@PathVariable Long id) {
        gradeClassService.deleteGradeClass(id);
        return ResponseEntity.ok(ApiResponse.okMessage("학년반 정보가 삭제되었습니다."));
    }

    @PostMapping(value = "/excel-upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<GradeClassDto.ExcelUploadResponse>> uploadExcel(
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(ApiResponse.ok(gradeClassService.uploadExcel(file)));
    }
}
