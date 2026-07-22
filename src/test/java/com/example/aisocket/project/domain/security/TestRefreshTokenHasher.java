package com.example.aisocket.project.domain.security;

public class TestRefreshTokenHasher implements RefreshTokenHasher {

    @Override
    public String hash(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            throw new IllegalArgumentException("리프레시 토큰(rawToken)은 필수 값입니다.");
        }
        return "hashed-token:" + rawToken;
    }

    @Override
    public boolean matches(String rawToken, String hashedToken) {
        return hash(rawToken).equals(hashedToken);
    }
}
