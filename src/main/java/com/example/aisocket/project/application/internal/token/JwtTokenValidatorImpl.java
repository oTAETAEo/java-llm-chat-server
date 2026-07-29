package com.example.aisocket.project.application.internal.token;

import com.example.aisocket.project.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class JwtTokenValidatorImpl implements JwtTokenValidator {

    private static final String REFRESH_TOKEN_TYPE = "refresh";

    private final JwtProperties jwtProperties;

    @Override
    public JwtTokenClaims validate(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return new JwtTokenClaims(
                Long.valueOf(claims.getSubject()),
                claims.get("email", String.class),
                claims.get("nickname", String.class),
                claims.get("tokenType", String.class)
        );
    }


    @Override
    public JwtTokenClaims validateRefreshToken(String token) {
        JwtTokenClaims claims = validate(token);
        if (!REFRESH_TOKEN_TYPE.equals(claims.tokenType())) {
            throw new IllegalArgumentException("리프레시 토큰이 아닙니다.");
        }
        return claims;
    }

    private SecretKey secretKey() {
        return Keys.hmacShaKeyFor(jwtProperties.secret().getBytes(StandardCharsets.UTF_8));
    }
}
