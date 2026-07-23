package com.example.aisocket.project.application.internal.token;

import com.example.aisocket.project.application.out.RefreshTokenRepository;
import com.example.aisocket.project.domain.Member;
import com.example.aisocket.project.domain.RefreshToken;
import com.example.aisocket.project.domain.security.RefreshTokenHasher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
public class RefreshTokenRegisterServiceImpl implements RefreshTokenRegisterService {

    private final RefreshTokenRepository refreshTokenRepository;

    private final RefreshTokenHasher refreshTokenHasher;

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
}
