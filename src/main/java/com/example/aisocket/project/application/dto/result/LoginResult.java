package com.example.aisocket.project.application.dto.result;

import java.time.Instant;

public record LoginResult(
        Long memberId,
        String email,
        String nickname,
        String accessToken,
        String refreshToken,
        Instant accessTokenExpiresAt,
        Instant refreshTokenExpiresAt
) {
}
