package com.school.healthroom.domain.commoncode.controller;

import com.school.healthroom.domain.commoncode.dto.CommonCodeDto;
import com.school.healthroom.domain.commoncode.service.CommonCodeService;
import com.school.healthroom.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/common-codes")
@RequiredArgsConstructor
public class CommonCodeController {

    private final CommonCodeService commonCodeService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CommonCodeDto.Response>>> getCommonCodes(
            @RequestParam(required = false) String groupCode,
            @RequestParam(required = false) Boolean useYn) {
        return ResponseEntity.ok(ApiResponse.ok(commonCodeService.getCommonCodes(groupCode, useYn)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CommonCodeDto.Response>> createCommonCode(
            @Valid @RequestBody CommonCodeDto.CreateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(commonCodeService.createCommonCode(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CommonCodeDto.Response>> updateCommonCode(
            @PathVariable Long id,
            @Valid @RequestBody CommonCodeDto.UpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(commonCodeService.updateCommonCode(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCommonCode(@PathVariable Long id) {
        commonCodeService.deleteCommonCode(id);
        return ResponseEntity.ok(ApiResponse.okMessage("공통코드가 삭제되었습니다."));
    }
}
