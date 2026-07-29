package com.example.aisocket.project.application.internal.token;

import com.example.aisocket.project.application.out.AccessTokenBlacklistRepository;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AccessTokenBlacklistServiceImpl implements AccessTokenBlacklistService {

    private final JwtTokenValidator tokenValidator;

    private final AccessTokenBlacklistRepository accessTokenBlacklistRepository;

    private final Clock clock;

    @Override
    public void blacklist(String rawAccessToken) {
        try {
            JwtTokenClaims claims = tokenValidator.validateAccessToken(rawAccessToken);
            Duration ttl = Duration.between(Instant.now(clock), claims.expiresAt());
            if (ttl.isPositive()) {
                accessTokenBlacklistRepository.save(rawAccessToken, ttl);
            }
        } catch (JwtException | IllegalArgumentException ignored) {
            // 이미 만료됐거나 잘못된 access token은 블랙리스트에 저장할 필요가 없다.
        }
    }

    @Override
    public boolean isBlacklisted(String rawAccessToken) {
        return accessTokenBlacklistRepository.exists(rawAccessToken);
    }
}
