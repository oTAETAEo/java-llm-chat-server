package com.example.aisocket.project.application.internal.token;

public record JwtTokenClaims(
        Long memberId,
        String email,
        String nickname,
        String tokenType
) {
}
