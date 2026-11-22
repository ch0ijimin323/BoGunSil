package com.school.healthroom.domain.auth.service;

import com.school.healthroom.domain.auth.dto.AuthDto;
import com.school.healthroom.domain.auth.entity.Admin;
import com.school.healthroom.domain.auth.repository.AdminRepository;
import com.school.healthroom.global.error.CustomException;
import com.school.healthroom.global.error.ErrorCode;
import com.school.healthroom.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public AuthDto.TokenResponse login(AuthDto.LoginRequest request) {
        Admin admin = adminRepository.findByLoginId(request.getLoginId())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_CREDENTIALS);
        }

        String accessToken = jwtTokenProvider.createAccessToken(admin.getLoginId(), admin.getName());
        String refreshToken = jwtTokenProvider.createRefreshToken(admin.getLoginId());
        admin.updateRefreshToken(refreshToken);

        return AuthDto.TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .loginId(admin.getLoginId())
                .name(admin.getName())
                .build();
    }

    @Transactional
    public AuthDto.TokenResponse reissue(AuthDto.ReissueRequest request) {
        String refreshToken = request.getRefreshToken();
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        String loginId = jwtTokenProvider.getLoginId(refreshToken);
        Admin admin = adminRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_TOKEN));

        if (admin.getRefreshToken() == null || !admin.getRefreshToken().equals(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        String newAccessToken = jwtTokenProvider.createAccessToken(admin.getLoginId(), admin.getName());
        String newRefreshToken = jwtTokenProvider.createRefreshToken(admin.getLoginId());
        admin.updateRefreshToken(newRefreshToken);

        return AuthDto.TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .loginId(admin.getLoginId())
                .name(admin.getName())
                .build();
    }

    @Transactional
    public void logout(String loginId) {
        adminRepository.findByLoginId(loginId)
                .ifPresent(admin -> admin.updateRefreshToken(null));
    }
}
