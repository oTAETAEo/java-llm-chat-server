package com.example.aisocket.project.domain.security;

import com.example.aisocket.project.adapter.out.security.Sha256RefreshTokenHasher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RefreshTokenHasherTest {

    private final RefreshTokenHasher refreshTokenHasher = new Sha256RefreshTokenHasher();

    @Test
    @DisplayName("리프레시 토큰 해시는 원문을 그대로 저장하지 않고 원문 검증을 지원한다")
    void hashAndMatchesRefreshToken() {
        String hashedToken = refreshTokenHasher.hash("raw-refresh-token");

        assertThat(hashedToken).isNotBlank();
        assertThat(hashedToken).isNotEqualTo("raw-refresh-token");
        assertThat(refreshTokenHasher.matches("raw-refresh-token", hashedToken)).isTrue();
        assertThat(refreshTokenHasher.matches("wrong-token", hashedToken)).isFalse();
    }

    @Test
    @DisplayName("원문 리프레시 토큰이 없으면 해시에 실패한다")
    void hashWithoutRawRefreshTokenFails() {
        assertThatThrownBy(() -> refreshTokenHasher.hash(" "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("리프레시 토큰");
    }

    @Test
    @DisplayName("검증 대상 값이 없으면 매칭에 실패한다")
    void matchesWithoutRequiredValueFails() {
        String hashedToken = refreshTokenHasher.hash("raw-refresh-token");

        assertThatThrownBy(() -> refreshTokenHasher.matches(" ", hashedToken))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("리프레시 토큰");
        assertThatThrownBy(() -> refreshTokenHasher.matches("raw-refresh-token", " "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("해시 리프레시 토큰");
    }
}
