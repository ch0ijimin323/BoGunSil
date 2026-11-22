package com.school.healthroom.domain.commoncode.dto;

import com.school.healthroom.domain.commoncode.entity.CommonCode;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class CommonCodeDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        @NotBlank(message = "그룹코드는 필수입니다.")
        private String groupCode;

        @NotBlank(message = "코드는 필수입니다.")
        private String code;

        @NotBlank(message = "코드명은 필수입니다.")
        private String codeName;

        private String parentCode;
        private Integer sortOrder;
        private Boolean useYn;
        private String description;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {
        @NotBlank(message = "코드명은 필수입니다.")
        private String codeName;

        private String parentCode;
        private Integer sortOrder;
        private Boolean useYn;
        private String description;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private String groupCode;
        private String code;
        private String codeName;
        private String parentCode;
        private Integer sortOrder;
        private Boolean useYn;
        private String description;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static Response from(CommonCode entity) {
            return Response.builder()
                    .id(entity.getId())
                    .groupCode(entity.getGroupCode())
                    .code(entity.getCode())
                    .codeName(entity.getCodeName())
                    .parentCode(entity.getParentCode())
                    .sortOrder(entity.getSortOrder())
                    .useYn(entity.getUseYn())
                    .description(entity.getDescription())
                    .createdAt(entity.getCreatedAt())
                    .updatedAt(entity.getUpdatedAt())
                    .build();
        }
    }
}
