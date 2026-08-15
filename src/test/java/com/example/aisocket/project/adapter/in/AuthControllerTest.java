package com.example.aisocket.project.adapter.in;

import com.example.aisocket.project.application.dto.command.LoginCommand;
import com.example.aisocket.project.application.dto.command.LogoutCommand;
import com.example.aisocket.project.application.dto.command.ReissueTokenCommand;
import com.example.aisocket.project.application.dto.command.SignUpMemberCommand;
import com.example.aisocket.project.application.dto.result.LoginResult;
import com.example.aisocket.project.application.dto.result.LogoutResult;
import com.example.aisocket.project.application.dto.result.ReissueTokenResult;
import com.example.aisocket.project.application.dto.result.SignUpMemberResult;
import com.example.aisocket.project.application.dto.result.TermsAgreementStatusResult;
import com.example.aisocket.project.application.dto.result.TermsDetailResult;
import com.example.aisocket.project.application.dto.result.TermsResult;
import com.example.aisocket.project.application.internal.terms.TermsAgreementStatusService;
import com.example.aisocket.project.application.internal.terms.TermsQueryService;
import com.example.aisocket.project.application.internal.token.JwtTokenClaims;
import com.example.aisocket.project.application.in.MemberAuthService;
import com.example.aisocket.project.common.error.MemberErrorCode;
import com.example.aisocket.project.common.error.ProjectException;
import com.example.aisocket.project.adapter.in.security.JwtAuthenticationFilter;
import com.example.aisocket.project.application.internal.token.AccessTokenBlacklistService;
import com.example.aisocket.project.application.internal.token.JwtTokenValidator;
import com.example.aisocket.project.application.out.MemberRepository;
import com.example.aisocket.project.config.SecurityConfig;
import com.example.aisocket.project.domain.MemberFixture;
import com.example.aisocket.project.domain.TermType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Instant;
import java.util.List;

import jakarta.servlet.http.Cookie;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtTokenValidator jwtTokenValidator;

    @MockitoBean
    private MemberRepository memberRepository;

    @MockitoBean
    private AccessTokenBlacklistService accessTokenBlacklistService;

    @MockitoBean
    private MemberAuthService memberAuthService;

    @MockitoBean
    private TermsQueryService termsQueryService;

    @MockitoBean
    private TermsAgreementStatusService termsAgreementStatusService;

    @Test
    @DisplayName("회원가입 약관 목록을 조회한다")
    void findSignUpTerms() throws Exception {
        given(termsQueryService.findActiveTerms()).willReturn(List.of(
                new TermsResult(1L, TermType.TERMS_OF_SERVICE, "terms-of-service-kr-v1", "이용약관", "2026.08.15", "/terms", true),
                new TermsResult(2L, TermType.PRIVACY_POLICY, "privacy-policy-kr-v1", "개인정보 처리방침", "2026.08.15", "/privacy", true)
        ));

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/v1/auth/terms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].termsId").value(1))
                .andExpect(jsonPath("$[0].required").value(true))
                .andExpect(jsonPath("$[1].code").value("privacy-policy-kr-v1"));
    }

    @Test
    @DisplayName("약관 코드를 기준으로 약관 본문을 조회한다")
    void findTerms() throws Exception {
        given(termsQueryService.findActiveTerm("privacy-policy-kr-v1"))
                .willReturn(new TermsDetailResult(
                        2L,
                        TermType.PRIVACY_POLICY,
                        "privacy-policy-kr-v1",
                        "개인정보 처리방침",
                        "2026.08.15",
                        "/privacy",
                        true,
                        "# 개인정보 처리방침"
                ));

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/v1/auth/terms/privacy-policy-kr-v1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("privacy-policy-kr-v1"))
                .andExpect(jsonPath("$.content").value("# 개인정보 처리방침"));
    }

    @Test
    @DisplayName("회원가입 요청을 처리한다")
    void signUp() throws Exception {

        given(memberAuthService.signUp(any(SignUpMemberCommand.class)))
                .willReturn(new SignUpMemberResult(1L, "runner@example.com", "runner"));

        mockMvc.perform(post("/api/v1/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signUpRequestJson()))
                .andExpect(status().isCreated());

        ArgumentCaptor<SignUpMemberCommand> commandCaptor =
                ArgumentCaptor.forClass(SignUpMemberCommand.class);

        verify(memberAuthService).signUp(commandCaptor.capture());
        assertThat(commandCaptor.getValue().email()).isEqualTo("runner@example.com");
        assertThat(commandCaptor.getValue().rawPassword()).isEqualTo("StrongPass1!");
        assertThat(commandCaptor.getValue().nickname()).isEqualTo("runner");
        assertThat(commandCaptor.getValue().agreedTermsIds()).containsExactly(1L, 2L);
    }

    @Test
    @DisplayName("회원가입 비밀번호가 강한 비밀번호 정책을 만족하지 않으면 요청에 실패한다")
    void signUpWithWeakPasswordFails() throws Exception {
        mockMvc.perform(post("/api/v1/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "runner@example.com",
                                  "password": "password",
                                  "nickname": "runner"
                }
                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("비밀번호는 8~64자이며 영문자, 숫자, 특수문자를 각각 1개 이상 포함하고 공백이 없어야 합니다."));

        verify(memberAuthService, never()).signUp(any(SignUpMemberCommand.class));
    }

    @Test
    @DisplayName("로그인 요청을 처리한다")
    void login() throws Exception {

        given(memberAuthService.login(any(LoginCommand.class)))
                .willReturn(new LoginResult(
                        1L,
                        "runner@example.com",
                        "runner",
                        agreedTermsStatus(),
                        "access-token",
                        "refresh-token",
                        Instant.parse("2026-07-24T00:30:00Z"),
                        Instant.parse("2026-08-07T00:00:00Z")
                ));

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestJson()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requiresTermsAgreement").value(false))
                .andExpect(jsonPath("$.missingRequiredTerms").isArray())
                .andReturn();

        List<String> cookies = mvcResult.getResponse().getHeaders(HttpHeaders.SET_COOKIE);
        assertThat(cookies).anySatisfy(cookie -> assertThat(cookie)
                .contains("accessToken=access-token", "Path=/", "HttpOnly", "SameSite=Lax"));
        assertThat(cookies).anySatisfy(cookie -> assertThat(cookie)
                .contains("refreshToken=refresh-token", "Path=/", "HttpOnly", "SameSite=Lax"));

        ArgumentCaptor<LoginCommand> commandCaptor = ArgumentCaptor.forClass(LoginCommand.class);
        verify(memberAuthService).login(commandCaptor.capture());
        assertThat(commandCaptor.getValue().email()).isEqualTo("runner@example.com");
        assertThat(commandCaptor.getValue().rawPassword()).isEqualTo("raw-password");
    }

    @Test
    @DisplayName("리프레시 토큰 쿠키로 토큰 재발급 요청을 처리한다")
    void reissueToken() throws Exception {

        given(memberAuthService.reissueToken(any(ReissueTokenCommand.class)))
                .willReturn(new ReissueTokenResult(
                        1L,
                        "runner@example.com",
                        "runner",
                        agreedTermsStatus(),
                        "new-access-token",
                        Instant.parse("2026-07-24T00:30:00Z")
                ));

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/auth/reissue")
                        .cookie(new Cookie("refreshToken", "old-refresh-token")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requiresTermsAgreement").value(false))
                .andReturn();

        List<String> cookies = mvcResult.getResponse().getHeaders(HttpHeaders.SET_COOKIE);
        assertThat(cookies).anySatisfy(cookie -> assertThat(cookie)
                .contains("accessToken=new-access-token", "Path=/", "HttpOnly", "SameSite=Lax"));
        assertThat(cookies).noneSatisfy(cookie -> assertThat(cookie).contains("refreshToken="));

        ArgumentCaptor<ReissueTokenCommand> commandCaptor = ArgumentCaptor.forClass(ReissueTokenCommand.class);
        verify(memberAuthService).reissueToken(commandCaptor.capture());
        assertThat(commandCaptor.getValue().refreshToken()).isEqualTo("old-refresh-token");
    }

    @Test
    @DisplayName("리프레시 토큰 쿠키로 로그아웃 요청을 처리하고 토큰 쿠키를 삭제한다")
    void logout() throws Exception {

        given(memberAuthService.logout(any(LogoutCommand.class)))
                .willReturn(new LogoutResult(1L));

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/auth/logout")
                        .cookie(new Cookie("accessToken", "access-token"))
                        .cookie(new Cookie("refreshToken", "refresh-token")))
                .andExpect(status().isOk())
                .andReturn();

        List<String> cookies = mvcResult.getResponse().getHeaders(HttpHeaders.SET_COOKIE);
        assertThat(cookies).anySatisfy(cookie -> assertThat(cookie)
                .contains("accessToken=", "Path=/", "Max-Age=0", "HttpOnly", "SameSite=Lax"));
        assertThat(cookies).anySatisfy(cookie -> assertThat(cookie)
                .contains("refreshToken=", "Path=/", "Max-Age=0", "HttpOnly", "SameSite=Lax"));

        ArgumentCaptor<LogoutCommand> commandCaptor = ArgumentCaptor.forClass(LogoutCommand.class);
        verify(memberAuthService).logout(commandCaptor.capture());
        assertThat(commandCaptor.getValue().accessToken()).isEqualTo("access-token");
        assertThat(commandCaptor.getValue().refreshToken()).isEqualTo("refresh-token");
    }

    @Test
    @DisplayName("인증 회원의 필수 약관 동의 상태를 조회한다")
    void findTermsAgreementStatus() throws Exception {
        givenAuthenticatedMember();
        given(termsAgreementStatusService.findStatus(1L))
                .willReturn(new TermsAgreementStatusResult(
                        true,
                        List.of(new TermsResult(
                                3L,
                                TermType.SENSITIVE_INFORMATION,
                                "sensitive-health-data-kr-v1",
                                "건강 관련 운동 데이터 처리 동의",
                                "2026.08.15",
                                "/privacy#sensitive-information",
                                true
                        ))
                ));

        mockMvc.perform(get("/api/v1/auth/terms/agreements/status")
                        .cookie(new Cookie("accessToken", "access-token")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requiresTermsAgreement").value(true))
                .andExpect(jsonPath("$.missingRequiredTerms[0].termsId").value(3));

        verify(termsAgreementStatusService).findStatus(1L);
    }

    @Test
    @DisplayName("인증 회원의 추가 약관 동의를 저장한다")
    void agreeTerms() throws Exception {
        givenAuthenticatedMember();
        given(termsAgreementStatusService.agree(1L, List.of(1L, 2L, 3L)))
                .willReturn(agreedTermsStatus());

        mockMvc.perform(post("/api/v1/auth/terms/agreements")
                        .cookie(new Cookie("accessToken", "access-token"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "agreedTermsIds": [1, 2, 3]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requiresTermsAgreement").value(false));

        verify(termsAgreementStatusService).agree(1L, List.of(1L, 2L, 3L));
    }

    @Test
    @DisplayName("서비스 예외를 공통 에러 응답으로 반환한다")
    void loginWithServiceException() throws Exception {
        given(memberAuthService.login(any(LoginCommand.class)))
                .willThrow(new ProjectException(MemberErrorCode.INVALID_CREDENTIALS));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestJson()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.code").value("MEMBER_INVALID_CREDENTIALS"))
                .andExpect(jsonPath("$.path").value("/api/v1/auth/login"));
    }

    @Test
    @DisplayName("리프레시 토큰 쿠키가 없으면 공통 인증 에러 응답을 반환한다")
    void reissueTokenWithoutRefreshTokenCookie() throws Exception {
        mockMvc.perform(post("/api/v1/auth/reissue"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.code").value("AUTHENTICATION_REQUIRED"))
                .andExpect(jsonPath("$.path").value("/api/v1/auth/reissue"));
    }

    private String signUpRequestJson() {
        return """
                {
                  "email": "runner@example.com",
                  "password": "StrongPass1!",
                  "nickname": "runner",
                  "agreedTermsIds": [1, 2]
                }
                """;
    }

    private String loginRequestJson() {
        return """
                {
                  "email": "runner@example.com",
                  "password": "raw-password"
                }
                """;
    }

    private TermsAgreementStatusResult agreedTermsStatus() {
        return new TermsAgreementStatusResult(false, List.of());
    }

    private void givenAuthenticatedMember() {
        given(jwtTokenValidator.validateAccessToken("access-token"))
                .willReturn(new JwtTokenClaims(
                        1L,
                        "runner@example.com",
                        "runner",
                        "access",
                        Instant.parse("2026-08-24T00:30:00Z")
                ));
        given(memberRepository.findById(1L))
                .willReturn(java.util.Optional.of(MemberFixture.builder()
                        .id(1L)
                        .email("runner@example.com")
                        .nickname("runner")
                        .build()));
    }
}
