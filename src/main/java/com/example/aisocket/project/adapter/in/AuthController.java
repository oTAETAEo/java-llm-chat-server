package com.example.aisocket.project.adapter.in;

import com.example.aisocket.project.adapter.in.dto.request.LoginRequest;
import com.example.aisocket.project.adapter.in.dto.request.LogoutRequest;
import com.example.aisocket.project.adapter.in.dto.request.ReissueTokenRequest;
import com.example.aisocket.project.adapter.in.dto.request.SignUpRequest;
import com.example.aisocket.project.adapter.in.dto.response.LoginResponse;
import com.example.aisocket.project.adapter.in.dto.response.LogoutResponse;
import com.example.aisocket.project.adapter.in.dto.response.ReissueTokenResponse;
import com.example.aisocket.project.adapter.in.dto.response.SignUpResponse;
import com.example.aisocket.project.application.dto.result.LoginResult;
import com.example.aisocket.project.application.dto.result.LogoutResult;
import com.example.aisocket.project.application.dto.result.ReissueTokenResult;
import com.example.aisocket.project.application.dto.result.SignUpMemberResult;
import com.example.aisocket.project.application.in.MemberAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
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
                .header(HttpHeaders.SET_COOKIE, createAccessTokenCookie(result.accessToken(), result.accessTokenExpiresAt()).toString())
                .header(HttpHeaders.SET_COOKIE, createRefreshTokenCookie(result.refreshToken(), result.refreshTokenExpiresAt()).toString())
                .body(LoginResponse.from(result));
    }

    @PostMapping("/reissue")
    public ResponseEntity<ReissueTokenResponse> reissueAccessToken(@CookieValue(REFRESH_TOKEN_COOKIE_NAME) String refreshToken) {

        ReissueTokenResult result = memberAuthService.reissueToken(
                ReissueTokenRequest.fromCookie(refreshToken).toCommand());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, createAccessTokenCookie(result.accessToken(), result.accessTokenExpiresAt()).toString())
                .body(ReissueTokenResponse.from(result));
    }

    @PostMapping("/logout")
    public ResponseEntity<LogoutResponse> logout(@CookieValue(REFRESH_TOKEN_COOKIE_NAME) String refreshToken) {

        LogoutResult result = memberAuthService.logout(LogoutRequest.fromCookie(refreshToken).toCommand());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteTokenCookie(ACCESS_TOKEN_COOKIE_NAME).toString())
                .header(HttpHeaders.SET_COOKIE, deleteTokenCookie(REFRESH_TOKEN_COOKIE_NAME).toString())
                .body(LogoutResponse.from(result));
    }

    private ResponseCookie createAccessTokenCookie(String token, Instant expiresAt) {
        return createTokenCookie(ACCESS_TOKEN_COOKIE_NAME, token, expiresAt);
    }

    private ResponseCookie createRefreshTokenCookie(String token, Instant expiresAt) {
        return createTokenCookie(REFRESH_TOKEN_COOKIE_NAME, token, expiresAt);
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

    private ResponseCookie deleteTokenCookie(String name) {
        return ResponseCookie.from(name, "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Lax")
                .maxAge(Duration.ZERO)
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
