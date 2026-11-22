package com.school.healthroom.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

public class AuthDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRequest {
        @NotBlank(message = "로그인 아이디는 필수입니다.")
        private String loginId;

        @NotBlank(message = "비밀번호는 필수입니다.")
        private String password;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReissueRequest {
        @NotBlank(message = "리프레시 토큰은 필수입니다.")
        private String refreshToken;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TokenResponse {
        private String accessToken;
        private String refreshToken;
        private String tokenType;
        private String loginId;
        private String name;
    }
}
