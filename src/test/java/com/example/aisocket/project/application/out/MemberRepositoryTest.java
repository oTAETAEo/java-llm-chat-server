package com.example.aisocket.project.application.out;

import com.example.aisocket.project.DataJpaTestSupport;
import com.example.aisocket.project.adapter.out.persistence.MemberRepositoryAdapter;
import com.example.aisocket.project.domain.Member;
import com.example.aisocket.project.domain.MemberFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Import(MemberRepositoryAdapter.class)
class MemberRepositoryTest extends DataJpaTestSupport {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("회원을 저장하고 ID로 조회한다")
    void saveAndFindById() {
        Member savedMember = memberRepository.save(MemberFixture.builder().nickname("runner").buildNew());

        Optional<Member> foundMember = memberRepository.findById(savedMember.getId());

        assertThat(savedMember.getId()).isNotNull();
        assertThat(foundMember).isPresent();
        assertThat(foundMember.get().getId()).isEqualTo(savedMember.getId());
        assertThat(foundMember.get().getNickname()).isEqualTo("runner");
    }
    @Test
    @DisplayName("인증 정보가 있는 회원을 저장하고 ID로 조회한다")
    void saveAndFindByIdWithAuthFields() {
        Member savedMember = memberRepository.save(
                MemberFixture.builder().id(null).nickname("runner").build()
        );

        Optional<Member> foundMember = memberRepository.findById(savedMember.getId());

        assertThat(savedMember.getId()).isNotNull();
        assertThat(foundMember).isPresent();
        assertThat(foundMember.get().getId()).isEqualTo(savedMember.getId());
        assertThat(foundMember.get().getEmail()).isEqualTo("runner@example.com");
        assertThat(foundMember.get().getPassword()).isEqualTo("encoded-password");
        assertThat(foundMember.get().getNickname()).isEqualTo("runner");
    }

    @Test
    @DisplayName("회원을 이메일로 조회한다")
    void findByEmail() {
        Member savedMember = memberRepository.save(
                MemberFixture.builder()
                        .email("runner@example.com")
                        .rawPassword("raw-password")
                        .nickname("runner")
                        .buildNew()
        );

        Optional<Member> foundMember = memberRepository.findByEmail("runner@example.com");

        assertThat(foundMember).isPresent();
        assertThat(foundMember.get().getId()).isEqualTo(savedMember.getId());
        assertThat(foundMember.get().getEmail()).isEqualTo("runner@example.com");
    }

    @Test
    @DisplayName("이메일 존재 여부를 확인한다")
    void existsByEmail() {
        memberRepository.save(
                MemberFixture.builder()
                        .email("runner@example.com")
                        .rawPassword("raw-password")
                        .nickname("runner")
                        .buildNew()
        );

        assertThat(memberRepository.existsByEmail("runner@example.com")).isTrue();
        assertThat(memberRepository.existsByEmail("unknown@example.com")).isFalse();
    }

    @Test
    @DisplayName("존재하지 않는 회원 ID를 조회하면 빈 Optional을 반환한다")
    void findByUnknownIdReturnsEmpty() {
        Optional<Member> foundMember = memberRepository.findById(999_999L);

        assertThat(foundMember).isEmpty();
    }

}
