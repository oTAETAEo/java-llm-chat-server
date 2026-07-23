package com.example.aisocket.project.application.out;

import com.example.aisocket.project.DataJpaTestSupport;
import com.example.aisocket.project.adapter.out.persistence.MemberRepositoryAdapter;
import com.example.aisocket.project.adapter.out.persistence.RefreshTokenRepositoryAdapter;
import com.example.aisocket.project.domain.Member;
import com.example.aisocket.project.domain.MemberFixture;
import com.example.aisocket.project.domain.RefreshToken;
import com.example.aisocket.project.domain.security.TestRefreshTokenHasher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Import({MemberRepositoryAdapter.class, RefreshTokenRepositoryAdapter.class})
class RefreshTokenRepositoryTest extends DataJpaTestSupport {

    private final TestRefreshTokenHasher refreshTokenHasher = new TestRefreshTokenHasher();

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Test
    @DisplayName("리프레시 토큰을 저장하고 ID로 조회한다")
    void saveAndFindById() {
        Member member = savedMember();
        RefreshToken savedRefreshToken = refreshTokenRepository.save(createRefreshToken(member, "refresh-token"));

        Optional<RefreshToken> foundRefreshToken = refreshTokenRepository.findById(savedRefreshToken.getId());

        assertThat(savedRefreshToken.getId()).isNotNull();
        assertThat(foundRefreshToken).isPresent();
        assertThat(foundRefreshToken.get().getId()).isEqualTo(savedRefreshToken.getId());
        assertThat(foundRefreshToken.get().getMemberId()).isEqualTo(member.getId());
        assertThat(foundRefreshToken.get().getToken()).isEqualTo("hashed-token:refresh-token");
        assertThat(foundRefreshToken.get().isRevoked()).isFalse();
    }

    @Test
    @DisplayName("해시된 토큰 값으로 리프레시 토큰을 조회한다")
    void findByToken() {
        Member member = savedMember();
        RefreshToken savedRefreshToken = refreshTokenRepository.save(createRefreshToken(member, "refresh-token"));

        Optional<RefreshToken> foundRefreshToken = refreshTokenRepository.findByToken("hashed-token:refresh-token");

        assertThat(foundRefreshToken).isPresent();
        assertThat(foundRefreshToken.get().getId()).isEqualTo(savedRefreshToken.getId());
    }

    @Test
    @DisplayName("존재하지 않는 리프레시 토큰 ID를 조회하면 빈 Optional을 반환한다")
    void findByUnknownIdReturnsEmpty() {
        Optional<RefreshToken> foundRefreshToken = refreshTokenRepository.findById(999_999L);

        assertThat(foundRefreshToken).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 토큰 값으로 조회하면 빈 Optional을 반환한다")
    void findByUnknownTokenReturnsEmpty() {
        Optional<RefreshToken> foundRefreshToken = refreshTokenRepository.findByToken("unknown-token");

        assertThat(foundRefreshToken).isEmpty();
    }

    private Member savedMember() {
        return memberRepository.save(
                MemberFixture.builder()
                        .id(null)
                        .email("runner@example.com")
                        .rawPassword("raw-password")
                        .nickname("runner")
                        .buildNew()
        );
    }

    private RefreshToken createRefreshToken(Member member, String rawToken) {
        return RefreshToken.create(
                member,
                rawToken,
                LocalDateTime.of(2026, 8, 7, 0, 0),
                refreshTokenHasher
        );
    }
}
