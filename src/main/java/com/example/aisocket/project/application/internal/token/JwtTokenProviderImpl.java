package com.example.aisocket.project.application.internal.token;

import com.example.aisocket.project.config.JwtProperties;
import com.example.aisocket.project.domain.Member;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProviderImpl implements JwtTokenProvider {

    private static final String ACCESS_TOKEN_TYPE = "access";
    private static final String REFRESH_TOKEN_TYPE = "refresh";

    private final JwtProperties jwtProperties;

    private final Clock clock;

    @Override
    public IssuedToken issue(Member member) {

        Instant issuedAt = Instant.now(clock);
        Instant accessTokenExpiresAt = issuedAt.plus(jwtProperties.accessTokenTtl());
        Instant refreshTokenExpiresAt = issuedAt.plus(jwtProperties.refreshTokenTtl());

        String accessToken = createToken(member, ACCESS_TOKEN_TYPE, issuedAt, accessTokenExpiresAt);
        String refreshToken = createToken(member, REFRESH_TOKEN_TYPE, issuedAt, refreshTokenExpiresAt);

        return new IssuedToken(accessToken, refreshToken, accessTokenExpiresAt, refreshTokenExpiresAt);
    }

    @Override
    public IssuedAccessToken issueAccessToken(Member member) {

        Instant issuedAt = Instant.now(clock);
        Instant accessTokenExpiresAt = issuedAt.plus(jwtProperties.accessTokenTtl());

        String accessToken = createToken(member, ACCESS_TOKEN_TYPE, issuedAt, accessTokenExpiresAt);

        return new IssuedAccessToken(accessToken, accessTokenExpiresAt);
    }

    private String createToken(Member member, String tokenType, Instant issuedAt, Instant expiresAt) {
        return Jwts.builder()
                .subject(String.valueOf(member.getId()))
                .claim("tokenType", tokenType)
                .claim("email", member.getEmail())
                .claim("nickname", member.getNickname())
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiresAt))
                .signWith(secretKey())
                .compact();
    }

    private SecretKey secretKey() {
        return Keys.hmacShaKeyFor(jwtProperties.secret().getBytes(StandardCharsets.UTF_8));
    }
}
