package com.example.aisocket.project.application.internal.token;

import com.example.aisocket.project.common.error.ProjectException;
import com.example.aisocket.project.config.JwtProperties;
import com.example.aisocket.project.domain.Member;
import com.example.aisocket.project.domain.MemberFixture;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtTokenValidatorTest {

    private static final String SECRET = "test-jwt-secret-key-must-be-at-least-32-bytes";
    private static final Instant NOW = Instant.parse("2026-08-24T00:00:00Z");

    private final JwtProperties jwtProperties = new JwtProperties(
            SECRET,
            Duration.ofMinutes(30),
            Duration.ofDays(14)
    );
    private final Clock clock = Clock.fixed(NOW, ZoneOffset.UTC);
    private final JwtTokenProvider jwtTokenProvider = new JwtTokenProviderImpl(jwtProperties, clock);
    private final JwtTokenValidator jwtTokenValidator = new JwtTokenValidatorImpl(jwtProperties);

    @Test
    @DisplayName("액세스 토큰을 검증하고 클레임을 반환한다")
    void validateAccessToken() {
        Member member = MemberFixture.builder()
                .id(1L)
                .email("runner@example.com")
                .nickname("runner")
                .build();
        IssuedToken issuedToken = jwtTokenProvider.issue(member);

        JwtTokenClaims claims = jwtTokenValidator.validate(issuedToken.accessToken());

        assertThat(claims.memberId()).isEqualTo(1L);
        assertThat(claims.email()).isEqualTo("runner@example.com");
        assertThat(claims.nickname()).isEqualTo("runner");
        assertThat(claims.tokenType()).isEqualTo("access");
    }

    @Test
    @DisplayName("리프레시 토큰을 검증하고 클레임을 반환한다")
    void validateRefreshToken() {
        Member member = MemberFixture.builder()
                .id(1L)
                .email("runner@example.com")
                .nickname("runner")
                .build();
        IssuedToken issuedToken = jwtTokenProvider.issue(member);

        JwtTokenClaims claims = jwtTokenValidator.validate(issuedToken.refreshToken());

        assertThat(claims.memberId()).isEqualTo(1L);
        assertThat(claims.email()).isEqualTo("runner@example.com");
        assertThat(claims.nickname()).isEqualTo("runner");
        assertThat(claims.tokenType()).isEqualTo("refresh");
    }

    @Test
    @DisplayName("리프레시 토큰 타입을 검증하고 클레임을 반환한다")
    void validateRefreshTokenType() {
        Member member = MemberFixture.builder()
                .id(1L)
                .email("runner@example.com")
                .nickname("runner")
                .build();
        IssuedToken issuedToken = jwtTokenProvider.issue(member);

        JwtTokenClaims claims = jwtTokenValidator.validateRefreshToken(issuedToken.refreshToken());

        assertThat(claims.memberId()).isEqualTo(1L);
        assertThat(claims.tokenType()).isEqualTo("refresh");
    }

    @Test
    @DisplayName("액세스 토큰을 리프레시 토큰으로 검증하면 실패한다")
    void validateRefreshTokenWithAccessTokenFails() {
        Member member = MemberFixture.builder()
                .id(1L)
                .email("runner@example.com")
                .nickname("runner")
                .build();
        IssuedToken issuedToken = jwtTokenProvider.issue(member);

        assertThatThrownBy(() -> jwtTokenValidator.validateRefreshToken(issuedToken.accessToken()))
                .isInstanceOf(ProjectException.class);
    }

    @Test
    @DisplayName("서명이 다른 토큰이면 검증에 실패한다")
    void validateTokenWithInvalidSignatureFails() {
        Member member = MemberFixture.builder()
                .id(1L)
                .email("runner@example.com")
                .nickname("runner")
                .build();
        IssuedToken issuedToken = jwtTokenProvider.issue(member);
        JwtProperties anotherJwtProperties = new JwtProperties(
                "another-jwt-secret-key-must-be-at-least-32-bytes",
                Duration.ofMinutes(30),
                Duration.ofDays(14)
        );
        JwtTokenValidator anotherJwtTokenValidator = new JwtTokenValidatorImpl(anotherJwtProperties);

        assertThatThrownBy(() -> anotherJwtTokenValidator.validate(issuedToken.accessToken()))
                .isInstanceOf(JwtException.class);
    }
}
