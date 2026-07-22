package com.example.aisocket.project.domain.security;

import com.example.aisocket.project.adapter.out.security.BCryptPasswordHasher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PasswordHasherTest {

    private final PasswordHasher passwordHasher = new BCryptPasswordHasher(new BCryptPasswordEncoder());

    @Test
    @DisplayName("비밀번호 해시는 원문을 그대로 저장하지 않고 원문 검증을 지원한다")
    void hashAndMatchesPassword() {
        String hashedPassword = passwordHasher.hash("password123!");

        assertThat(hashedPassword).isNotBlank();
        assertThat(hashedPassword).isNotEqualTo("password123!");
        assertThat(passwordHasher.matches("password123!", hashedPassword)).isTrue();
        assertThat(passwordHasher.matches("wrong-password", hashedPassword)).isFalse();
    }

    @Test
    @DisplayName("원문 비밀번호가 없으면 해시에 실패한다")
    void hashWithoutRawPasswordFails() {
        assertThatThrownBy(() -> passwordHasher.hash(" "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("비밀번호");
    }

    @Test
    @DisplayName("검증 대상 값이 없으면 매칭에 실패한다")
    void matchesWithoutRequiredValueFails() {
        String hashedPassword = passwordHasher.hash("password123!");

        assertThatThrownBy(() -> passwordHasher.matches(" ", hashedPassword))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("비밀번호");
        assertThatThrownBy(() -> passwordHasher.matches("password123!", " "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("해시 비밀번호");
    }
}
