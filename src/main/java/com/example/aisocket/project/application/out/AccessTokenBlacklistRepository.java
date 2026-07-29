package com.example.aisocket.project.application.out;

import java.time.Duration;

public interface AccessTokenBlacklistRepository {

    void save(String accessToken, Duration ttl);

    boolean exists(String accessToken);
}
