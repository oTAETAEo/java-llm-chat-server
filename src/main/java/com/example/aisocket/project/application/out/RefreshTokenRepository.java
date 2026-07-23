package com.example.aisocket.project.application.out;

import com.example.aisocket.project.domain.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository {

    RefreshToken save(RefreshToken refreshToken);

    Optional<RefreshToken> findById(Long id);

    Optional<RefreshToken> findByToken(String token);

}
