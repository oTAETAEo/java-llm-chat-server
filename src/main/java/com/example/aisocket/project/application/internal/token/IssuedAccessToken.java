package com.example.aisocket.project.application.internal.token;

import java.time.Instant;

public record IssuedAccessToken(
        String accessToken,
        Instant accessTokenExpiresAt
) {
}
