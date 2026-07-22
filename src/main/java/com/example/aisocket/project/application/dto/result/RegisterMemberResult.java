package com.example.aisocket.project.application.dto.result;

public record RegisterMemberResult(
        Long memberId,
        String email,
        String nickname
) {
}
