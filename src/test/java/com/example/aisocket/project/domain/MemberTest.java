package com.example.aisocket.project.domain;

import com.example.aisocket.project.domain.security.TestPasswordHasher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MemberTest {

    private final TestPasswordHasher passwordHasher = new TestPasswordHasher();

    @Test
    @DisplayName("신규 회원 생성 시 비밀번호를 해시해서 저장한다")
    void createMember() {
        Member member = Member.create("runner@example.com", "raw-password", "runner", passwordHasher);

        assertThat(member.getId()).isNull();
        assertThat(member.getEmail()).isEqualTo("runner@example.com");
        assertThat(member.getPassword()).isEqualTo("hashed-password:raw-password");
        assertThat(member.getNickname()).isEqualTo("runner");
    }

    @Test
    @DisplayName("ID가 있는 회원 도메인을 생성한다")
    void createMemberWithId() {
        Member member = Member.of(1L, "runner@example.com", "encoded-password", "runner");

        assertThat(member.getId()).isEqualTo(1L);
        assertThat(member.getEmail()).isEqualTo("runner@example.com");
        assertThat(member.getPassword()).isEqualTo("encoded-password");
        assertThat(member.getNickname()).isEqualTo("runner");
    }

    @Test
    @DisplayName("닉네임이 없으면 회원 생성에 실패한다")
    void createMemberWithoutNicknameFails() {
        assertThatThrownBy(() -> Member.create("runner@example.com", "raw-password", null, passwordHasher))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("닉네임");
    }

    @Test
    @DisplayName("닉네임이 공백이면 회원 생성에 실패한다")
    void createMemberWithBlankNicknameFails() {
        assertThatThrownBy(() -> Member.create("runner@example.com", "raw-password", " ", passwordHasher))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("닉네임");
    }

    @Test
    @DisplayName("인증 정보 생성 시 이메일이 없으면 실패한다")
    void createMemberWithAuthFieldsWithoutEmailFails() {
        assertThatThrownBy(() -> Member.create(null, "raw-password", "runner", passwordHasher))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이메일");
    }

    @Test
    @DisplayName("인증 정보 생성 시 이메일 형식이 올바르지 않으면 실패한다")
    void createMemberWithAuthFieldsWithInvalidEmailFails() {
        assertThatThrownBy(() -> Member.create("runner", "raw-password", "runner", passwordHasher))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이메일");
    }

    @Test
    @DisplayName("인증 정보 생성 시 비밀번호가 없으면 실패한다")
    void createMemberWithAuthFieldsWithoutPasswordFails() {
        assertThatThrownBy(() -> Member.create("runner@example.com", null, "runner", passwordHasher))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("비밀번호");
    }

    @Test
    @DisplayName("비밀번호 해시 정책이 없으면 회원 생성에 실패한다")
    void createMemberWithoutPasswordHasherFails() {
        assertThatThrownBy(() -> Member.create("runner@example.com", "raw-password", "runner", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("비밀번호 해시 정책");
    }
}
