package com.example.aisocket.project.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MemberTest {

    @Test
    @DisplayName("신규 회원을 생성한다")
    void createMember() {
        Member member = Member.create("runner");

        assertThat(member.getId()).isNull();
        assertThat(member.getNickname()).isEqualTo("runner");
    }

    @Test
    @DisplayName("ID가 있는 회원 도메인을 생성한다")
    void createMemberWithId() {
        Member member = Member.of(1L, "runner");

        assertThat(member.getId()).isEqualTo(1L);
        assertThat(member.getNickname()).isEqualTo("runner");
    }

    @Test
    @DisplayName("닉네임이 없으면 회원 생성에 실패한다")
    void createMemberWithoutNicknameFails() {
        assertThatThrownBy(() -> Member.create(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("닉네임");
    }

    @Test
    @DisplayName("닉네임이 공백이면 회원 생성에 실패한다")
    void createMemberWithBlankNicknameFails() {
        assertThatThrownBy(() -> Member.create(" "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("닉네임");
    }
}
