package com.example.aisocket.project.domain.security;

public interface RefreshTokenHasher {

    String hash(String rawToken);

    boolean matches(String rawToken, String hashedToken);
}
