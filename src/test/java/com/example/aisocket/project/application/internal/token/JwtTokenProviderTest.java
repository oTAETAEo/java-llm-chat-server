package com.example.aisocket.project.application.internal.token;

import com.example.aisocket.project.config.JwtProperties;
import com.example.aisocket.project.domain.Member;
import com.example.aisocket.project.domain.MemberFixture;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenProviderTest {

    private static final String SECRET = "test-jwt-secret-key-must-be-at-least-32-bytes";
    private static final Instant NOW = Instant.parse("2026-08-24T00:00:00Z");

    private final JwtProperties jwtProperties = new JwtProperties(
            SECRET,
            Duration.ofMinutes(30),
            Duration.ofDays(14)
    );
    private final Clock clock = Clock.fixed(NOW, ZoneOffset.UTC);
    private final JwtTokenProvider tokenProvider = new JwtTokenProviderImpl(jwtProperties, clock);

    @Test
    @DisplayName("회원 정보로 액세스 토큰과 리프레시 토큰을 발급한다")
    void issue() {
        Member member = MemberFixture.builder()
                .id(1L)
                .email("runner@example.com")
                .nickname("runner")
                .build();

        IssuedToken issuedToken = tokenProvider.issue(member);

        Claims accessClaims = parseClaims(issuedToken.accessToken());
        Claims refreshClaims = parseClaims(issuedToken.refreshToken());

        assertThat(issuedToken.accessToken()).isNotBlank();
        assertThat(issuedToken.refreshToken()).isNotBlank();
        assertThat(issuedToken.accessTokenExpiresAt()).isEqualTo(NOW.plus(Duration.ofMinutes(30)));
        assertThat(issuedToken.refreshTokenExpiresAt()).isEqualTo(NOW.plus(Duration.ofDays(14)));
        assertThat(accessClaims.getSubject()).isEqualTo("1");
        assertThat(accessClaims.get("tokenType", String.class)).isEqualTo("access");
        assertThat(accessClaims.get("email", String.class)).isEqualTo("runner@example.com");
        assertThat(accessClaims.get("nickname", String.class)).isEqualTo("runner");
        assertThat(refreshClaims.getSubject()).isEqualTo("1");
        assertThat(refreshClaims.get("tokenType", String.class)).isEqualTo("refresh");
    }

    @Test
    @DisplayName("회원 정보로 액세스 토큰만 발급한다")
    void issueAccessToken() {
        Member member = MemberFixture.builder()
                .id(1L)
                .email("runner@example.com")
                .nickname("runner")
                .build();

        IssuedAccessToken issuedAccessToken = tokenProvider.issueAccessToken(member);

        Claims accessClaims = parseClaims(issuedAccessToken.accessToken());

        assertThat(issuedAccessToken.accessToken()).isNotBlank();
        assertThat(issuedAccessToken.accessTokenExpiresAt()).isEqualTo(NOW.plus(Duration.ofMinutes(30)));
        assertThat(accessClaims.getSubject()).isEqualTo("1");
        assertThat(accessClaims.get("tokenType", String.class)).isEqualTo("access");
        assertThat(accessClaims.get("email", String.class)).isEqualTo("runner@example.com");
        assertThat(accessClaims.get("nickname", String.class)).isEqualTo("runner");
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey secretKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }
}
