package com.example.aisocket.project.application.internal.token;

import com.example.aisocket.project.application.out.RefreshTokenRepository;
import com.example.aisocket.project.domain.Member;
import com.example.aisocket.project.domain.RefreshToken;
import com.example.aisocket.project.domain.security.RefreshTokenHasher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RefreshTokenRegisterServiceImpl implements RefreshTokenRegisterService {

    private final RefreshTokenRepository refreshTokenRepository;

    private final RefreshTokenHasher refreshTokenHasher;

    private final JwtTokenValidator tokenValidator;

    private final Clock clock;

    @Override
    @Transactional
    public RefreshToken register(Member member, IssuedToken issuedToken) {

        RefreshToken refreshToken = RefreshToken.create(
                member,
                issuedToken.refreshToken(),
                LocalDateTime.ofInstant(issuedToken.refreshTokenExpiresAt(), ZoneOffset.UTC),
                refreshTokenHasher
        );

        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    @Transactional(readOnly = true)
    public RefreshToken findUsable(String rawRefreshToken) {

        JwtTokenClaims claims = tokenValidator.validateRefreshToken(rawRefreshToken);

        RefreshToken refreshToken = findSavedRefreshToken(rawRefreshToken);

        validateOwner(refreshToken, claims.memberId());
        validateUsable(refreshToken);

        return refreshToken;
    }

    @Override
    @Transactional
    public RefreshToken revoke(String rawRefreshToken) {

        JwtTokenClaims claims = tokenValidator.validateRefreshToken(rawRefreshToken);

        RefreshToken refreshToken = findSavedRefreshToken(rawRefreshToken);

        validateOwner(refreshToken, claims.memberId());
        refreshToken.revoke();

        return refreshTokenRepository.save(refreshToken);
    }

    private RefreshToken findSavedRefreshToken(String rawRefreshToken) {

        String hashedToken = refreshTokenHasher.hash(rawRefreshToken);

        return refreshTokenRepository.findByToken(hashedToken)
                .orElseThrow(() -> new IllegalArgumentException("저장된 리프레시 토큰을 찾을 수 없습니다."));
    }

    private void validateOwner(RefreshToken refreshToken, Long memberId) {

        if (!Objects.equals(refreshToken.getMemberId(), memberId)) {
            throw new IllegalArgumentException("리프레시 토큰 소유자가 일치하지 않습니다.");
        }
    }

    private void validateUsable(RefreshToken refreshToken) {

        LocalDateTime now = LocalDateTime.ofInstant(clock.instant(), ZoneOffset.UTC);

        if (!refreshToken.isUsable(now)) {
            throw new IllegalArgumentException("사용할 수 없는 리프레시 토큰입니다.");
        }
    }
}
