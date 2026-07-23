package com.example.aisocket.project.application.dto.result;

public record SignUpMemberResult(
        Long memberId,
        String email,
        String nickname
) {
}
