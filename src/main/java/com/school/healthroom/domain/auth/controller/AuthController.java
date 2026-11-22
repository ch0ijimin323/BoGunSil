package com.school.healthroom.domain.auth.controller;

import com.school.healthroom.domain.auth.dto.AuthDto;
import com.school.healthroom.domain.auth.service.AuthService;
import com.school.healthroom.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthDto.TokenResponse>> login(@Valid @RequestBody AuthDto.LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(authService.login(request)));
    }

    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<AuthDto.TokenResponse>> reissue(@Valid @RequestBody AuthDto.ReissueRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(authService.reissue(request)));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(Authentication authentication) {
        if (authentication != null && authentication.getName() != null) {
            authService.logout(authentication.getName());
        }
        return ResponseEntity.ok(ApiResponse.okMessage("로그아웃되었습니다."));
    }
}
