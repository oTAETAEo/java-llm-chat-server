package com.example.aisocket.project.application.service;

import com.example.aisocket.project.SpringBootIntegrationTestSupport;
import com.example.aisocket.project.application.dto.command.LoginCommand;
import com.example.aisocket.project.application.dto.command.SignUpMemberCommand;
import com.example.aisocket.project.application.dto.result.LoginResult;
import com.example.aisocket.project.application.dto.result.SignUpMemberResult;
import com.example.aisocket.project.application.in.MemberAuthService;
import com.example.aisocket.project.application.internal.member.MemberFinderService;
import com.example.aisocket.project.application.internal.member.MemberRegisterService;
import com.example.aisocket.project.application.internal.token.IssuedToken;
import com.example.aisocket.project.application.internal.token.RefreshTokenRegisterService;
import com.example.aisocket.project.application.internal.token.TokenProvider;
import com.example.aisocket.project.domain.Member;
import com.example.aisocket.project.domain.MemberFixture;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class MemberAuthServiceTest extends SpringBootIntegrationTestSupport {

    @Autowired
    private MemberAuthService memberAuthService;

    @MockitoBean
    private MemberFinderService memberFinderService;

    @MockitoBean
    private MemberRegisterService memberRegisterService;

    @MockitoBean
    private TokenProvider tokenProvider;

    @MockitoBean
    private RefreshTokenRegisterService refreshTokenRegisterService;

    @Test
    @DisplayName("회원가입 시 회원 등록 서비스를 호출하고 결과를 반환한다")
    void registerMember() {
        SignUpMemberCommand command = new SignUpMemberCommand("runner@example.com", "raw-password", "runner");
        when(memberRegisterService.register(command)).thenReturn(MemberFixture.builder()
                .id(1L)
                .email(command.email())
                .encodedPassword("hashed-password:raw-password")
                .nickname(command.nickname())
                .build());

        SignUpMemberResult result = memberAuthService.signUp(command);

        verify(memberFinderService).validateNotExistsByEmail(command.email());
        verify(memberRegisterService).register(command);
        assertThat(result.memberId()).isEqualTo(1L);
        assertThat(result.email()).isEqualTo("runner@example.com");
        assertThat(result.nickname()).isEqualTo("runner");
    }

    @Test
    @DisplayName("이미 사용 중인 이메일이면 회원가입에 실패한다")
    void registerMemberWithDuplicatedEmailFails() {
        SignUpMemberCommand command = new SignUpMemberCommand("runner@example.com", "raw-password", "runner");

        doThrow(new IllegalArgumentException("이미 사용 중인 이메일입니다."))
                .when(memberFinderService).validateNotExistsByEmail("runner@example.com");

        assertThatThrownBy(() -> memberAuthService.signUp(command))
                .isInstanceOf(IllegalArgumentException.class);

        verify(memberRegisterService, never()).register(any(SignUpMemberCommand.class));
    }

    @Test
    @DisplayName("회원가입 요청이 없으면 서비스 검증에 실패한다")
    void registerWithoutCommandFails() {
        assertThatThrownBy(() -> memberAuthService.signUp(null))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    @DisplayName("회원가입 요청 필드가 비어 있으면 서비스 검증에 실패한다")
    void registerWithInvalidCommandFails() {
        SignUpMemberCommand command = new SignUpMemberCommand(" ", "", "");

        assertThatThrownBy(() -> memberAuthService.signUp(command))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    @DisplayName("로그인 시 비밀번호를 검증하고 토큰을 발급한다")
    void login() {
        LoginCommand command = new LoginCommand("runner@example.com", "raw-password");
        Member member = MemberFixture.builder()
                .id(1L)
                .email(command.email())
                .encodedPassword("encoded-password")
                .nickname("runner")
                .build();
        IssuedToken issuedToken = issuedToken();
        when(memberFinderService.findLoginMember(command)).thenReturn(member);
        when(tokenProvider.issue(member)).thenReturn(issuedToken);

        LoginResult result = memberAuthService.login(command);

        verify(memberFinderService).findLoginMember(command);
        verify(tokenProvider).issue(member);
        verify(refreshTokenRegisterService).register(member, issuedToken);
        assertThat(result.memberId()).isEqualTo(1L);
        assertThat(result.email()).isEqualTo("runner@example.com");
        assertThat(result.nickname()).isEqualTo("runner");
        assertThat(result.accessToken()).isEqualTo("access-token");
        assertThat(result.refreshToken()).isEqualTo("refresh-token");
        assertThat(result.accessTokenExpiresAt()).isEqualTo(Instant.parse("2026-07-24T00:30:00Z"));
        assertThat(result.refreshTokenExpiresAt()).isEqualTo(Instant.parse("2026-08-07T00:00:00Z"));
    }

    @Test
    @DisplayName("로그인 요청이 없으면 서비스 검증에 실패한다")
    void loginWithoutCommandFails() {
        assertThatThrownBy(() -> memberAuthService.login(null))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    @DisplayName("로그인 요청 필드가 비어 있으면 서비스 검증에 실패한다")
    void loginWithInvalidCommandFails() {
        LoginCommand command = new LoginCommand(" ", "");

        assertThatThrownBy(() -> memberAuthService.login(command))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    @DisplayName("로그인 회원 조회에 실패하면 로그인에 실패한다")
    void loginWithInvalidCredentialFails() {
        LoginCommand command = new LoginCommand("runner@example.com", "wrong-password");
        when(memberFinderService.findLoginMember(command))
                .thenThrow(new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다."));

        assertThatThrownBy(() -> memberAuthService.login(command))
                .isInstanceOf(IllegalArgumentException.class);

        verifyNoInteractions(tokenProvider, refreshTokenRegisterService);
    }

    private IssuedToken issuedToken() {
        return new IssuedToken(
                "access-token",
                "refresh-token",
                Instant.parse("2026-07-24T00:30:00Z"),
                Instant.parse("2026-08-07T00:00:00Z")
        );
    }
}
