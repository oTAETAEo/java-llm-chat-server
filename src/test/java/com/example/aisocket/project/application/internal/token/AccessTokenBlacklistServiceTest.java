package com.example.aisocket.project.application.internal.token;

import com.example.aisocket.project.application.out.AccessTokenBlacklistRepository;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AccessTokenBlacklistServiceTest {

    private static final Instant NOW = Instant.parse("2026-08-24T00:00:00Z");

    private final JwtTokenValidator tokenValidator = mock(JwtTokenValidator.class);
    private final AccessTokenBlacklistRepository accessTokenBlacklistRepository = mock(AccessTokenBlacklistRepository.class);
    private final Clock clock = Clock.fixed(NOW, ZoneOffset.UTC);
    private final AccessTokenBlacklistService accessTokenBlacklistService =
            new AccessTokenBlacklistServiceImpl(tokenValidator, accessTokenBlacklistRepository, clock);

    @Test
    @DisplayName("액세스 토큰의 남은 만료 시간만큼 블랙리스트에 저장한다")
    void blacklist() {
        when(tokenValidator.validateAccessToken("access-token"))
                .thenReturn(accessClaims(NOW.plus(Duration.ofMinutes(30))));

        accessTokenBlacklistService.blacklist("access-token");

        verify(accessTokenBlacklistRepository).save(eq("access-token"), eq(Duration.ofMinutes(30)));
    }

    @Test
    @DisplayName("이미 만료된 액세스 토큰은 블랙리스트에 저장하지 않는다")
    void blacklistWithExpiredAccessToken() {
        when(tokenValidator.validateAccessToken("access-token"))
                .thenReturn(accessClaims(NOW.minus(Duration.ofMinutes(1))));

        accessTokenBlacklistService.blacklist("access-token");

        verify(accessTokenBlacklistRepository, never()).save(eq("access-token"), org.mockito.ArgumentMatchers.any());
    }

    @Test
    @DisplayName("검증할 수 없는 액세스 토큰은 블랙리스트에 저장하지 않는다")
    void blacklistWithInvalidAccessToken() {
        when(tokenValidator.validateAccessToken("invalid-token"))
                .thenThrow(new JwtException("invalid token"));

        accessTokenBlacklistService.blacklist("invalid-token");

        verify(accessTokenBlacklistRepository, never()).save(eq("invalid-token"), org.mockito.ArgumentMatchers.any());
    }

    @Test
    @DisplayName("액세스 토큰 블랙리스트 포함 여부를 조회한다")
    void isBlacklisted() {
        when(accessTokenBlacklistRepository.exists("access-token")).thenReturn(true);

        boolean blacklisted = accessTokenBlacklistService.isBlacklisted("access-token");

        assertThat(blacklisted).isTrue();
    }

    private JwtTokenClaims accessClaims(Instant expiresAt) {
        return new JwtTokenClaims(1L, "runner@example.com", "runner", "access", expiresAt);
    }
}
