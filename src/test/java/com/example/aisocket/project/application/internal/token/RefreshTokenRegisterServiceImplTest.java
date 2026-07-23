package com.example.aisocket.project.application.internal.token;

import com.example.aisocket.project.application.out.RefreshTokenRepository;
import com.example.aisocket.project.domain.Member;
import com.example.aisocket.project.domain.MemberFixture;
import com.example.aisocket.project.domain.RefreshToken;
import com.example.aisocket.project.domain.security.TestRefreshTokenHasher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RefreshTokenRegisterServiceImplTest {

    private final RefreshTokenRepository refreshTokenRepository = mock(RefreshTokenRepository.class);
    private final TestRefreshTokenHasher refreshTokenHasher = new TestRefreshTokenHasher();
    private final RefreshTokenRegisterService refreshTokenRegisterService =
            new RefreshTokenRegisterServiceImpl(refreshTokenRepository, refreshTokenHasher);

    @Test
    @DisplayName("발급된 JWT 리프레시 토큰을 해시해서 저장한다")
    void register() {
        Member member = MemberFixture.builder().id(1L).nickname("runner").build();
        IssuedToken issuedToken = new IssuedToken(
                "access-token",
                "refresh-jwt-token",
                Instant.parse("2026-07-24T00:30:00Z"),
                Instant.parse("2026-08-07T00:00:00Z")
        );
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RefreshToken refreshToken = refreshTokenRegisterService.register(member, issuedToken);

        verify(refreshTokenRepository).save(any(RefreshToken.class));
        assertThat(refreshToken.getMember()).isSameAs(member);
        assertThat(refreshToken.getToken()).isEqualTo("hashed-token:refresh-jwt-token");
        assertThat(refreshToken.getExpiresAt()).isEqualTo(LocalDateTime.of(2026, 8, 7, 0, 0));
        assertThat(refreshToken.isRevoked()).isFalse();
    }
}
