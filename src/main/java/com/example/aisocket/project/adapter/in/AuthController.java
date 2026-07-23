package com.example.aisocket.project.adapter.in;

import com.example.aisocket.project.adapter.in.dto.request.LoginRequest;
import com.example.aisocket.project.adapter.in.dto.request.SignUpRequest;
import com.example.aisocket.project.adapter.in.dto.response.LoginResponse;
import com.example.aisocket.project.adapter.in.dto.response.SignUpResponse;
import com.example.aisocket.project.application.dto.result.LoginResult;
import com.example.aisocket.project.application.dto.result.SignUpMemberResult;
import com.example.aisocket.project.application.in.MemberAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.Instant;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final String ACCESS_TOKEN_COOKIE_NAME = "accessToken";
    private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";

    private final MemberAuthService memberAuthService;

    @PostMapping("/sign-up")
    public ResponseEntity<SignUpResponse> signUp(@RequestBody SignUpRequest request) {

        SignUpMemberResult result = memberAuthService.signUp(request.toCommand());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SignUpResponse.from(result));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {

        LoginResult result = memberAuthService.login(request.toCommand());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, createAccessTokenCookie(result).toString())
                .header(HttpHeaders.SET_COOKIE, createRefreshTokenCookie(result).toString())
                .body(LoginResponse.from(result));
    }

    private ResponseCookie createAccessTokenCookie(LoginResult result) {
        return createTokenCookie(
                ACCESS_TOKEN_COOKIE_NAME,
                result.accessToken(),
                result.accessTokenExpiresAt()
        );
    }

    private ResponseCookie createRefreshTokenCookie(LoginResult result) {
        return createTokenCookie(
                REFRESH_TOKEN_COOKIE_NAME,
                result.refreshToken(),
                result.refreshTokenExpiresAt()
        );
    }

    private ResponseCookie createTokenCookie(String name, String value, Instant expiresAt) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Lax")
                .maxAge(calculateMaxAge(expiresAt))
                .build();
    }

    private Duration calculateMaxAge(Instant expiresAt) {
        Duration maxAge = Duration.between(Instant.now(), expiresAt);
        if (maxAge.isNegative()) {
            return Duration.ZERO;
        }
        return maxAge;
    }
}
