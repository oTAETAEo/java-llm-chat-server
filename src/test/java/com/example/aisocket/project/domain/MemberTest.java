package com.example.aisocket.project.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MemberTest {

    @Test
    @DisplayName("신규 회원 생성 시 비밀번호를 해시해서 저장한다")
    void createMember() {
        Member member = MemberFixture.builder()
                .email("runner@example.com")
                .rawPassword("raw-password")
                .nickname("runner")
                .buildNew();

        assertThat(member.getId()).isNull();
        assertThat(member.getEmail()).isEqualTo("runner@example.com");
        assertThat(member.getPassword()).isEqualTo("hashed-password:raw-password");
        assertThat(member.getNickname()).isEqualTo("runner");
    }

    @Test
    @DisplayName("ID가 있는 회원 도메인을 생성한다")
    void createMemberWithId() {
        Member member = MemberFixture.builder()
                .id(1L)
                .email("runner@example.com")
                .encodedPassword("encoded-password")
                .nickname("runner")
                .build();

        assertThat(member.getId()).isEqualTo(1L);
        assertThat(member.getEmail()).isEqualTo("runner@example.com");
        assertThat(member.getPassword()).isEqualTo("encoded-password");
        assertThat(member.getNickname()).isEqualTo("runner");
    }

    @Test
    @DisplayName("닉네임이 없으면 회원 생성에 실패한다")
    void createMemberWithoutNicknameFails() {
        assertThatThrownBy(() -> MemberFixture.builder().nickname(null).buildNew())
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("닉네임이 공백이면 회원 생성에 실패한다")
    void createMemberWithBlankNicknameFails() {
        assertThatThrownBy(() -> MemberFixture.builder().nickname(" ").buildNew())
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("인증 정보 생성 시 이메일이 없으면 실패한다")
    void createMemberWithAuthFieldsWithoutEmailFails() {
        assertThatThrownBy(() -> MemberFixture.builder().email(null).buildNew())
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("인증 정보 생성 시 이메일 형식이 올바르지 않으면 실패한다")
    void createMemberWithAuthFieldsWithInvalidEmailFails() {
        assertThatThrownBy(() -> MemberFixture.builder().email("runner").buildNew())
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("인증 정보 생성 시 비밀번호가 없으면 실패한다")
    void createMemberWithAuthFieldsWithoutPasswordFails() {
        assertThatThrownBy(() -> MemberFixture.builder().rawPassword(null).buildNew())
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("비밀번호 해시 정책이 없으면 회원 생성에 실패한다")
    void createMemberWithoutPasswordHasherFails() {
        assertThatThrownBy(() -> MemberFixture.builder().buildNewWithoutPasswordHasher())
                .isInstanceOf(IllegalArgumentException.class);
    }
}
