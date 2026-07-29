package com.example.aisocket.project.application.internal.token;

import com.example.aisocket.project.application.out.RefreshTokenRepository;
import com.example.aisocket.project.domain.Member;
import com.example.aisocket.project.domain.MemberFixture;
import com.example.aisocket.project.domain.RefreshToken;
import com.example.aisocket.project.domain.security.TestRefreshTokenHasher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RefreshTokenRegisterServiceTest {

    private static final Instant NOW = Instant.parse("2026-07-24T00:00:00Z");

    private final RefreshTokenRepository refreshTokenRepository = mock(RefreshTokenRepository.class);
    private final TestRefreshTokenHasher refreshTokenHasher = new TestRefreshTokenHasher();
    private final JwtTokenValidator tokenValidator = mock(JwtTokenValidator.class);
    private final Clock clock = Clock.fixed(NOW, ZoneOffset.UTC);
    private final RefreshTokenRegisterService refreshTokenRegisterService =
            new RefreshTokenRegisterServiceImpl(refreshTokenRepository, refreshTokenHasher, tokenValidator, clock);

    @Test
    @DisplayName("발급된 JWT 리프레시 토큰을 해시해서 저장한다")
    void register() {
        Member member = MemberFixture.builder().id(1L).nickname("runner").build();
        IssuedToken issuedToken = new IssuedToken(
                "access-token",
                "refresh-jwt-token",
                Instant.parse("2026-07-24T00:30:00Z"),
                Instant.parse("2026-08-07T00:00:00Z")
        );
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RefreshToken refreshToken = refreshTokenRegisterService.register(member, issuedToken);

        verify(refreshTokenRepository).save(any(RefreshToken.class));
        assertThat(refreshToken.getMember()).isSameAs(member);
        assertThat(refreshToken.getToken()).isEqualTo("hashed-token:refresh-jwt-token");
        assertThat(refreshToken.getExpiresAt()).isEqualTo(LocalDateTime.of(2026, 8, 7, 0, 0));
        assertThat(refreshToken.isRevoked()).isFalse();
    }

    @Test
    @DisplayName("사용 가능한 리프레시 토큰을 조회한다")
    void findUsable() {
        Member member = MemberFixture.builder().id(1L).build();
        RefreshToken refreshToken = refreshToken(member, "refresh-token", LocalDateTime.of(2026, 8, 7, 0, 0));
        when(tokenValidator.validateRefreshToken("refresh-token")).thenReturn(refreshClaims(1L));
        when(refreshTokenRepository.findByToken("hashed-token:refresh-token")).thenReturn(Optional.of(refreshToken));

        RefreshToken foundRefreshToken = refreshTokenRegisterService.findUsable("refresh-token");

        assertThat(foundRefreshToken).isSameAs(refreshToken);
    }

    @Test
    @DisplayName("폐기된 리프레시 토큰은 사용 가능 조회에 실패한다")
    void findUsableWithRevokedTokenFails() {
        Member member = MemberFixture.builder().id(1L).build();
        RefreshToken refreshToken = refreshToken(member, "refresh-token", LocalDateTime.of(2026, 8, 7, 0, 0));
        refreshToken.revoke();
        when(tokenValidator.validateRefreshToken("refresh-token")).thenReturn(refreshClaims(1L));
        when(refreshTokenRepository.findByToken("hashed-token:refresh-token")).thenReturn(Optional.of(refreshToken));

        assertThatThrownBy(() -> refreshTokenRegisterService.findUsable("refresh-token"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("만료된 리프레시 토큰은 사용 가능 조회에 실패한다")
    void findUsableWithExpiredTokenFails() {
        Member member = MemberFixture.builder().id(1L).build();
        RefreshToken refreshToken = refreshToken(member, "refresh-token", LocalDateTime.of(2026, 7, 23, 0, 0));
        when(tokenValidator.validateRefreshToken("refresh-token")).thenReturn(refreshClaims(1L));
        when(refreshTokenRepository.findByToken("hashed-token:refresh-token")).thenReturn(Optional.of(refreshToken));

        assertThatThrownBy(() -> refreshTokenRegisterService.findUsable("refresh-token"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("리프레시 토큰 소유자가 다르면 조회에 실패한다")
    void findUsableWithDifferentOwnerFails() {
        Member member = MemberFixture.builder().id(1L).build();
        RefreshToken refreshToken = refreshToken(member, "refresh-token", LocalDateTime.of(2026, 8, 7, 0, 0));
        when(tokenValidator.validateRefreshToken("refresh-token")).thenReturn(refreshClaims(2L));
        when(refreshTokenRepository.findByToken("hashed-token:refresh-token")).thenReturn(Optional.of(refreshToken));

        assertThatThrownBy(() -> refreshTokenRegisterService.findUsable("refresh-token"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("raw 리프레시 토큰과 회원 ID로 저장된 토큰을 폐기한다")
    void revokeByRawToken() {
        Member member = MemberFixture.builder().id(1L).build();
        RefreshToken refreshToken = refreshToken(member, "refresh-token", LocalDateTime.of(2026, 8, 7, 0, 0));
        when(tokenValidator.validateRefreshToken("refresh-token")).thenReturn(refreshClaims(1L));
        when(refreshTokenRepository.findByToken("hashed-token:refresh-token")).thenReturn(Optional.of(refreshToken));
        when(refreshTokenRepository.save(refreshToken)).thenReturn(refreshToken);

        RefreshToken revokedRefreshToken = refreshTokenRegisterService.revoke("refresh-token");

        verify(refreshTokenRepository).save(refreshToken);
        assertThat(revokedRefreshToken.isRevoked()).isTrue();
    }

    @Test
    @DisplayName("JWT 타입이 리프레시 토큰이 아니면 실패한다")
    void findUsableWithAccessTokenFails() {
        when(tokenValidator.validateRefreshToken("access-token"))
                .thenThrow(new IllegalArgumentException("리프레시 토큰이 아닙니다."));

        assertThatThrownBy(() -> refreshTokenRegisterService.findUsable("access-token"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private JwtTokenClaims refreshClaims(Long memberId) {
        return new JwtTokenClaims(memberId, "runner@example.com", "runner", "refresh", Instant.parse("2026-08-24T00:30:00Z"));
    }

    private RefreshToken refreshToken(Member member, String rawRefreshToken, LocalDateTime expiresAt) {
        return RefreshToken.create(member, rawRefreshToken, expiresAt, refreshTokenHasher);
    }
}
