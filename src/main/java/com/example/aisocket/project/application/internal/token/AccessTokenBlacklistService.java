package com.example.aisocket.project.application.internal.token;

public interface AccessTokenBlacklistService {

    void blacklist(String rawAccessToken);

    boolean isBlacklisted(String rawAccessToken);
}
