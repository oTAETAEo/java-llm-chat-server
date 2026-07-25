package com.example.aisocket.project.application.internal.member;

import com.example.aisocket.project.application.dto.command.LoginCommand;
import com.example.aisocket.project.application.out.MemberRepository;
import com.example.aisocket.project.domain.Member;
import com.example.aisocket.project.domain.MemberFixture;
import com.example.aisocket.project.domain.security.TestPasswordHasher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MemberFinderServiceTest {

    private final MemberRepository memberRepository = mock(MemberRepository.class);
    private final TestPasswordHasher passwordHasher = new TestPasswordHasher();
    private final MemberFinderService memberFinderService = new MemberFinderServiceImpl(memberRepository, passwordHasher);

    @Test
    @DisplayName("로그인 요청의 이메일과 비밀번호가 일치하면 회원을 반환한다")
    void findLoginMember() {
        LoginCommand command = new LoginCommand("runner@example.com", "raw-password");
        Member member = MemberFixture.builder()
                .id(1L)
                .email(command.email())
                .encodedPassword("hashed-password:raw-password")
                .nickname("runner")
                .build();
        when(memberRepository.findByEmail(command.email())).thenReturn(Optional.of(member));

        Member foundMember = memberFinderService.findLoginMember(command);

        assertThat(foundMember).isSameAs(member);
    }

    @Test
    @DisplayName("로그인 이메일에 해당하는 회원이 없으면 실패한다")
    void findLoginMemberWithoutEmailFails() {
        LoginCommand command = new LoginCommand("runner@example.com", "raw-password");
        when(memberRepository.findByEmail(command.email())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> memberFinderService.findLoginMember(command))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("로그인 비밀번호가 일치하지 않으면 실패한다")
    void findLoginMemberWithInvalidPasswordFails() {
        LoginCommand command = new LoginCommand("runner@example.com", "wrong-password");
        Member member = MemberFixture.builder()
                .id(1L)
                .email(command.email())
                .encodedPassword("hashed-password:raw-password")
                .nickname("runner")
                .build();
        when(memberRepository.findByEmail(command.email())).thenReturn(Optional.of(member));

        assertThatThrownBy(() -> memberFinderService.findLoginMember(command))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
