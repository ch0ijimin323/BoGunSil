package com.school.healthroom.domain.commoncode.service;

import com.school.healthroom.domain.commoncode.dto.CommonCodeDto;
import com.school.healthroom.domain.commoncode.entity.CommonCode;
import com.school.healthroom.domain.commoncode.repository.CommonCodeRepository;
import com.school.healthroom.global.error.CustomException;
import com.school.healthroom.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommonCodeService {

    private final CommonCodeRepository commonCodeRepository;

    public List<CommonCodeDto.Response> getCommonCodes(String groupCode, Boolean useYn) {
        return commonCodeRepository.search(groupCode, useYn)
                .stream()
                .map(CommonCodeDto.Response::from)
                .toList();
    }

    @Transactional
    public CommonCodeDto.Response createCommonCode(CommonCodeDto.CreateRequest request) {
        if (commonCodeRepository.existsByGroupCodeAndCode(request.getGroupCode(), request.getCode())) {
            throw new CustomException(ErrorCode.DUPLICATE_RESOURCE, "이미 존재하는 그룹코드 및 코드입니다.");
        }

        CommonCode commonCode = CommonCode.builder()
                .groupCode(request.getGroupCode())
                .code(request.getCode())
                .codeName(request.getCodeName())
                .parentCode(request.getParentCode())
                .sortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0)
                .useYn(request.getUseYn() != null ? request.getUseYn() : true)
                .description(request.getDescription())
                .build();

        return CommonCodeDto.Response.from(commonCodeRepository.save(commonCode));
    }

    @Transactional
    public CommonCodeDto.Response updateCommonCode(Long id, CommonCodeDto.UpdateRequest request) {
        CommonCode commonCode = commonCodeRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "공통코드를 찾을 수 없습니다."));

        commonCode.update(
                request.getCodeName(),
                request.getParentCode(),
                request.getSortOrder(),
                request.getUseYn(),
                request.getDescription()
        );
        return CommonCodeDto.Response.from(commonCode);
    }

    @Transactional
    public void deleteCommonCode(Long id) {
        CommonCode commonCode = commonCodeRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "공통코드를 찾을 수 없습니다."));
        commonCodeRepository.delete(commonCode);
    }
}
