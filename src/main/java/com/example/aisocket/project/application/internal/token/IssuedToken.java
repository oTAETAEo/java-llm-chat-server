package com.example.aisocket.project.application.internal.token;

import java.time.Instant;

public record IssuedToken(
        String accessToken,
        String refreshToken,
        Instant accessTokenExpiresAt,
        Instant refreshTokenExpiresAt
) {
}
