package com.example.aisocket.project.adapter.out.cache;

import com.example.aisocket.project.application.out.AccessTokenBlacklistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.HexFormat;

@Component
@RequiredArgsConstructor
public class RedisAccessTokenBlacklistRepository implements AccessTokenBlacklistRepository {

    private static final String KEY_PREFIX = "blacklist:access:";
    private static final String BLACKLISTED_VALUE = "1";
    private static final String HASH_ALGORITHM = "SHA-256";

    private final StringRedisTemplate redisTemplate;

    @Override
    public void save(String accessToken, Duration ttl) {
        if (ttl == null || ttl.isZero() || ttl.isNegative()) {
            return;
        }
        redisTemplate.opsForValue().set(key(accessToken), BLACKLISTED_VALUE, ttl);
    }

    @Override
    public boolean exists(String accessToken) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key(accessToken)));
    }

    private String key(String accessToken) {
        return KEY_PREFIX + hash(accessToken);
    }

    private String hash(String accessToken) {
        try {
            byte[] digest = MessageDigest.getInstance(HASH_ALGORITHM)
                    .digest(accessToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(HASH_ALGORITHM + " 해시 알고리즘을 사용할 수 없습니다.", e);
        }
    }
}
