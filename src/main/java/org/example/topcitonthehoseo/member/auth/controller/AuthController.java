package org.example.topcitonthehoseo.member.auth.controller;

import org.example.topcitonthehoseo.member.auth.dto.GoogleLoginRequestDto;
import org.example.topcitonthehoseo.member.auth.dto.LoginRequestDto;
import org.example.topcitonthehoseo.member.auth.dto.LoginResponseDto;
import org.example.topcitonthehoseo.member.auth.dto.RefreshTokenRequestDto;
import org.example.topcitonthehoseo.member.auth.dto.RegisterRequestDto;
import org.example.topcitonthehoseo.member.auth.dto.TokenResponseDto;
import org.example.topcitonthehoseo.member.auth.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/member/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 일반 로그인
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto requestDto) {
        return ResponseEntity.ok(authService.login(requestDto));
    }

    // 구글 로그인
    @PostMapping("/google-login")
    public ResponseEntity<LoginResponseDto> googleLogin(@RequestBody GoogleLoginRequestDto requestDto) {
        return ResponseEntity.ok(authService.googleLogin(requestDto));
    }

    // 회원가입 (구글 최초 로그인 후 추가 정보 등록)
    @PostMapping("/register")
    public ResponseEntity<LoginResponseDto> register(@RequestBody RegisterRequestDto requestDto) {
        return ResponseEntity.ok(authService.register(requestDto));
    }

    // 토큰 재발급
    @PostMapping("/refresh-token")
    public ResponseEntity<TokenResponseDto> refreshToken(@RequestBody RefreshTokenRequestDto request) {
        TokenResponseDto newTokens = authService.reissueToken(request.getRefreshToken());
        return ResponseEntity.ok(newTokens);
    }
}