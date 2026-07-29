package com.example.aisocket.project.application.internal.token;

import java.time.Instant;

public record JwtTokenClaims(
        Long memberId,
        String email,
        String nickname,
        String tokenType,
        Instant expiresAt
) {
}
