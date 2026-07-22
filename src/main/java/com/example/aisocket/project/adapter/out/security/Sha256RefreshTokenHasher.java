package com.example.aisocket.project.adapter.out.security;

import com.example.aisocket.project.domain.security.RefreshTokenHasher;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Component
public class Sha256RefreshTokenHasher implements RefreshTokenHasher {

    private static final String ALGORITHM = "SHA-256";

    @Override
    public String hash(String rawToken) {
        validateRequired(rawToken, "리프레시 토큰(rawToken)");
        return HexFormat.of().formatHex(digest(rawToken));
    }

    @Override
    public boolean matches(String rawToken, String hashedToken) {
        validateRequired(rawToken, "리프레시 토큰(rawToken)");
        validateRequired(hashedToken, "해시 리프레시 토큰(hashedToken)");
        return MessageDigest.isEqual(hash(rawToken).getBytes(StandardCharsets.UTF_8), hashedToken.getBytes(StandardCharsets.UTF_8));
    }

    private byte[] digest(String rawToken) {
        try {
            return MessageDigest.getInstance(ALGORITHM).digest(rawToken.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(ALGORITHM + " 해시 알고리즘을 사용할 수 없습니다.", e);
        }
    }

    private void validateRequired(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + "은 필수 값입니다.");
        }
    }
}
