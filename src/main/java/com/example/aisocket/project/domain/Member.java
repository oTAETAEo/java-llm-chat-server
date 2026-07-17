package com.example.aisocket.project.domain;

import lombok.Getter;

@Getter
public class Member {

    private final Long id;
    private final String nickname;

    public static Member create(String nickname) {
        return new Member(null, nickname);
    }

    public static Member of(Long id, String nickname) {
        return new Member(id, nickname);
    }

    private Member(Long id, String nickname) {
        this.id = id;
        this.nickname = nickname;

        validate();
    }

    private void validate() {
        if (nickname == null || nickname.isBlank()) {
            throw new IllegalArgumentException("닉네임(nickname)은 필수 값입니다.");
        }
    }
}
