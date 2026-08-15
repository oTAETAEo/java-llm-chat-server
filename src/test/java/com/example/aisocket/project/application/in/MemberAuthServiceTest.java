package com.example.aisocket.project.application.in;

import com.example.aisocket.project.SpringBootIntegrationTestSupport;
import com.example.aisocket.project.application.dto.command.LoginCommand;
import com.example.aisocket.project.application.dto.command.LogoutCommand;
import com.example.aisocket.project.application.dto.command.ReissueTokenCommand;
import com.example.aisocket.project.application.dto.command.SignUpMemberCommand;
import com.example.aisocket.project.application.dto.result.LoginResult;
import com.example.aisocket.project.application.dto.result.LogoutResult;
import com.example.aisocket.project.application.dto.result.ReissueTokenResult;
import com.example.aisocket.project.application.dto.result.SignUpMemberResult;
import com.example.aisocket.project.application.dto.result.TermsAgreementStatusResult;
import com.example.aisocket.project.application.internal.member.MemberFinderService;
import com.example.aisocket.project.application.internal.member.MemberRegisterService;
import com.example.aisocket.project.application.internal.terms.MemberTermsAgreementRegisterService;
import com.example.aisocket.project.application.internal.terms.TermsAgreementStatusService;
import com.example.aisocket.project.application.internal.token.AccessTokenBlacklistService;
import com.example.aisocket.project.application.internal.token.IssuedAccessToken;
import com.example.aisocket.project.application.internal.token.IssuedToken;
import com.example.aisocket.project.application.internal.token.RefreshTokenRegisterService;
import com.example.aisocket.project.application.internal.token.JwtTokenProvider;
import com.example.aisocket.project.common.error.MemberErrorCode;
import com.example.aisocket.project.common.error.ProjectException;
import com.example.aisocket.project.common.error.TokenErrorCode;
import com.example.aisocket.project.domain.Member;
import com.example.aisocket.project.domain.RefreshToken;
import com.example.aisocket.project.domain.security.TestRefreshTokenHasher;
import com.example.aisocket.project.domain.MemberFixture;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

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
    private MemberTermsAgreementRegisterService memberTermsAgreementRegisterService;

    @MockitoBean
    private TermsAgreementStatusService termsAgreementStatusService;

    @MockitoBean
    private JwtTokenProvider tokenProvider;

    @MockitoBean
    private RefreshTokenRegisterService refreshTokenRegisterService;

    @MockitoBean
    private AccessTokenBlacklistService accessTokenBlacklistService;

    @Test
    @DisplayName("회원가입 시 회원 등록 서비스를 호출하고 결과를 반환한다")
    void registerMember() {
        SignUpMemberCommand command = new SignUpMemberCommand("runner@example.com", "StrongPass1!", "runner", List.of(1L, 2L));
        when(memberRegisterService.register(command)).thenReturn(MemberFixture.builder()
                .id(1L)
                .email(command.email())
                .encodedPassword("hashed-password:StrongPass1!")
                .nickname(command.nickname())
                .build());

        SignUpMemberResult result = memberAuthService.signUp(command);

        verify(memberFinderService).validateNotExistsByEmail(command.email());
        verify(memberRegisterService).register(command);
        verify(memberTermsAgreementRegisterService).register(any(Member.class), any());
        assertThat(result.memberId()).isEqualTo(1L);
        assertThat(result.email()).isEqualTo("runner@example.com");
        assertThat(result.nickname()).isEqualTo("runner");
    }

    @Test
    @DisplayName("이미 사용 중인 이메일이면 회원가입에 실패한다")
    void registerMemberWithDuplicatedEmailFails() {
        SignUpMemberCommand command = new SignUpMemberCommand("runner@example.com", "StrongPass1!", "runner", List.of(1L, 2L));

        doThrow(new ProjectException(MemberErrorCode.DUPLICATED_EMAIL))
                .when(memberFinderService).validateNotExistsByEmail("runner@example.com");

        assertThatThrownBy(() -> memberAuthService.signUp(command))
                .isInstanceOf(ProjectException.class);

        verify(memberRegisterService, never()).register(any(SignUpMemberCommand.class));
        verifyNoInteractions(memberTermsAgreementRegisterService);
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
        SignUpMemberCommand command = new SignUpMemberCommand(" ", "", "", List.of());

        assertThatThrownBy(() -> memberAuthService.signUp(command))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    @DisplayName("회원가입 비밀번호가 강한 비밀번호 정책을 만족하지 않으면 서비스 검증에 실패한다")
    void registerWithWeakPasswordFails() {
        SignUpMemberCommand command = new SignUpMemberCommand("runner@example.com", "password", "runner", List.of(1L, 2L));

        assertThatThrownBy(() -> memberAuthService.signUp(command))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("영문자, 숫자, 특수문자");
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
        when(termsAgreementStatusService.findStatus(member.getId()))
                .thenReturn(agreedTermsStatus());

        LoginResult result = memberAuthService.login(command);

        verify(memberFinderService).findLoginMember(command);
        verify(tokenProvider).issue(member);
        verify(refreshTokenRegisterService).register(member, issuedToken);
        verify(termsAgreementStatusService).findStatus(member.getId());
        assertThat(result.memberId()).isEqualTo(1L);
        assertThat(result.email()).isEqualTo("runner@example.com");
        assertThat(result.nickname()).isEqualTo("runner");
        assertThat(result.termsAgreementStatus().requiresTermsAgreement()).isFalse();
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
                .thenThrow(new ProjectException(MemberErrorCode.INVALID_CREDENTIALS));

        assertThatThrownBy(() -> memberAuthService.login(command))
                .isInstanceOf(ProjectException.class);

        verifyNoInteractions(tokenProvider, refreshTokenRegisterService);
    }

    @Test
    @DisplayName("리프레시 토큰 재발급 시 기존 토큰을 폐기하고 새 토큰을 발급한다")
    void reissueToken() {
        ReissueTokenCommand command = new ReissueTokenCommand("old-refresh-token");
        Member member = MemberFixture.builder()
                .id(1L)
                .email("runner@example.com")
                .nickname("runner")
                .build();
        IssuedAccessToken newIssuedAccessToken = new IssuedAccessToken(
                "new-access-token",
                Instant.parse("2026-07-24T00:30:00Z")
        );
        when(refreshTokenRegisterService.findUsable(command.refreshToken())).thenReturn(refreshToken(member));
        when(memberFinderService.findById(1L)).thenReturn(member);
        when(tokenProvider.issueAccessToken(member)).thenReturn(newIssuedAccessToken);
        when(termsAgreementStatusService.findStatus(member.getId()))
                .thenReturn(agreedTermsStatus());

        ReissueTokenResult result = memberAuthService.reissueToken(command);

        verify(refreshTokenRegisterService).findUsable(command.refreshToken());
        verify(memberFinderService).findById(1L);
        verify(tokenProvider).issueAccessToken(member);
        verify(termsAgreementStatusService).findStatus(member.getId());
        assertThat(result.memberId()).isEqualTo(1L);
        assertThat(result.accessToken()).isEqualTo("new-access-token");
        assertThat(result.termsAgreementStatus().requiresTermsAgreement()).isFalse();
    }

    @Test
    @DisplayName("리프레시 토큰 검증에 실패하면 토큰 재발급에 실패한다")
    void reissueTokenWithInvalidRefreshTokenFails() {
        ReissueTokenCommand command = new ReissueTokenCommand("invalid-refresh-token");
        when(refreshTokenRegisterService.findUsable(command.refreshToken()))
                .thenThrow(new ProjectException(TokenErrorCode.REFRESH_TOKEN_UNUSABLE));

        assertThatThrownBy(() -> memberAuthService.reissueToken(command))
                .isInstanceOf(ProjectException.class);

        verify(refreshTokenRegisterService).findUsable(command.refreshToken());
        verifyNoInteractions(memberFinderService, tokenProvider);
    }

    @Test
    @DisplayName("저장된 리프레시 토큰이 폐기 상태이면 토큰 재발급에 실패한다")
    void reissueTokenWithRevokedTokenFails() {
        ReissueTokenCommand command = new ReissueTokenCommand("old-refresh-token");
        when(refreshTokenRegisterService.findUsable(command.refreshToken()))
                .thenThrow(new ProjectException(TokenErrorCode.REFRESH_TOKEN_UNUSABLE));

        assertThatThrownBy(() -> memberAuthService.reissueToken(command))
                .isInstanceOf(ProjectException.class);

        verify(refreshTokenRegisterService).findUsable(command.refreshToken());
        verifyNoInteractions(memberFinderService, tokenProvider);
    }

    @Test
    @DisplayName("로그아웃 시 저장된 리프레시 토큰을 폐기한다")
    void logout() {
        LogoutCommand command = new LogoutCommand("access-token", "refresh-token");
        Member member = MemberFixture.builder().id(1L).build();
        RefreshToken savedRefreshToken = refreshToken(member);
        when(refreshTokenRegisterService.revoke(command.refreshToken())).thenReturn(savedRefreshToken);

        LogoutResult result = memberAuthService.logout(command);

        verify(refreshTokenRegisterService).revoke(command.refreshToken());
        verify(accessTokenBlacklistService).blacklist(command.accessToken());
        assertThat(result.memberId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("로그아웃 요청이 없으면 서비스 검증에 실패한다")
    void logoutWithoutCommandFails() {
        assertThatThrownBy(() -> memberAuthService.logout(null))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    @DisplayName("토큰 재발급 요청이 없으면 서비스 검증에 실패한다")
    void reissueTokenWithoutCommandFails() {
        assertThatThrownBy(() -> memberAuthService.reissueToken(null))
                .isInstanceOf(ConstraintViolationException.class);
    }

    private IssuedToken issuedToken() {
        return new IssuedToken(
                "access-token",
                "refresh-token",
                Instant.parse("2026-07-24T00:30:00Z"),
                Instant.parse("2026-08-07T00:00:00Z")
        );
    }

    private TermsAgreementStatusResult agreedTermsStatus() {
        return new TermsAgreementStatusResult(false, List.of());
    }

    private RefreshToken refreshToken(Member member) {
        return RefreshToken.create(
                member,
                "refresh-token",
                LocalDateTime.of(2026, 8, 7, 0, 0),
                new TestRefreshTokenHasher()
        );
    }

}
