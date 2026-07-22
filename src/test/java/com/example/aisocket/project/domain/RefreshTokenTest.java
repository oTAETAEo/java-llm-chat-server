package com.example.aisocket.project.domain;

import com.example.aisocket.project.domain.security.TestRefreshTokenHasher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RefreshTokenTest {

    private final TestRefreshTokenHasher refreshTokenHasher = new TestRefreshTokenHasher();

    @Test
    @DisplayName("리프레시 토큰을 해시해서 생성한다")
    void createRefreshToken() {
        Member member = member();
        LocalDateTime expiresAt = LocalDateTime.of(2026, 7, 29, 0, 0);

        RefreshToken refreshToken = RefreshToken.create(member, "refresh-token", expiresAt, refreshTokenHasher);

        assertThat(refreshToken.getId()).isNull();
        assertThat(refreshToken.getMember()).isSameAs(member);
        assertThat(refreshToken.getMemberId()).isEqualTo(1L);
        assertThat(refreshToken.getToken()).isEqualTo("hashed-token:refresh-token");
        assertThat(refreshToken.getExpiresAt()).isEqualTo(expiresAt);
        assertThat(refreshToken.isRevoked()).isFalse();
        assertThat(refreshToken.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("리프레시 토큰을 폐기한다")
    void revokeRefreshToken() {
        RefreshToken refreshToken = createRefreshToken(LocalDateTime.of(2026, 7, 29, 0, 0));

        refreshToken.revoke();

        assertThat(refreshToken.isRevoked()).isTrue();
    }

    @Test
    @DisplayName("만료 시간이 현재 시간 이후이면 사용할 수 있다")
    void usableRefreshToken() {
        RefreshToken refreshToken = createRefreshToken(LocalDateTime.of(2026, 7, 29, 0, 0));

        assertThat(refreshToken.isExpired(LocalDateTime.of(2026, 7, 28, 23, 59))).isFalse();
        assertThat(refreshToken.isUsable(LocalDateTime.of(2026, 7, 28, 23, 59))).isTrue();
    }

    @Test
    @DisplayName("만료 시간이 현재 시간과 같거나 이전이면 만료된 토큰이다")
    void expiredRefreshToken() {
        RefreshToken refreshToken = createRefreshToken(LocalDateTime.of(2026, 7, 29, 0, 0));

        assertThat(refreshToken.isExpired(LocalDateTime.of(2026, 7, 29, 0, 0))).isTrue();
        assertThat(refreshToken.isUsable(LocalDateTime.of(2026, 7, 29, 0, 0))).isFalse();
    }

    @Test
    @DisplayName("폐기된 토큰은 사용할 수 없다")
    void revokedRefreshTokenIsNotUsable() {
        RefreshToken refreshToken = createRefreshToken(LocalDateTime.of(2026, 7, 29, 0, 0));
        refreshToken.revoke();

        assertThat(refreshToken.isUsable(LocalDateTime.of(2026, 7, 28, 0, 0))).isFalse();
    }

    @Test
    @DisplayName("회원이 없으면 리프레시 토큰 생성에 실패한다")
    void createRefreshTokenWithoutMemberFails() {
        assertThatThrownBy(() -> RefreshToken.create(null, "refresh-token", LocalDateTime.of(2026, 7, 29, 0, 0), refreshTokenHasher))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("회원");
    }

    @Test
    @DisplayName("회원 ID가 없으면 리프레시 토큰 생성에 실패한다")
    void createRefreshTokenWithoutMemberIdFails() {
        Member unsavedMember = Member.of(null, "runner@example.com", "encoded-password", "runner");

        assertThatThrownBy(() -> RefreshToken.create(unsavedMember, "refresh-token", LocalDateTime.of(2026, 7, 29, 0, 0), refreshTokenHasher))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("회원 ID");
    }

    @Test
    @DisplayName("토큰 값이 없으면 리프레시 토큰 생성에 실패한다")
    void createRefreshTokenWithoutTokenFails() {
        assertThatThrownBy(() -> RefreshToken.create(member(), " ", LocalDateTime.of(2026, 7, 29, 0, 0), refreshTokenHasher))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("리프레시 토큰");
    }

    @Test
    @DisplayName("만료 시간이 없으면 리프레시 토큰 생성에 실패한다")
    void createRefreshTokenWithoutExpiresAtFails() {
        assertThatThrownBy(() -> RefreshToken.create(member(), "refresh-token", null, refreshTokenHasher))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("만료 시간");
    }

    @Test
    @DisplayName("리프레시 토큰 해시 정책이 없으면 생성에 실패한다")
    void createRefreshTokenWithoutHasherFails() {
        assertThatThrownBy(() -> RefreshToken.create(member(), "refresh-token", LocalDateTime.of(2026, 7, 29, 0, 0), null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("리프레시 토큰 해시 정책");
    }

    private RefreshToken createRefreshToken(LocalDateTime expiresAt) {
        return RefreshToken.create(member(), "refresh-token", expiresAt, refreshTokenHasher);
    }

    private Member member() {
        return Member.of(1L, "runner@example.com", "encoded-password", "runner");
    }
}
