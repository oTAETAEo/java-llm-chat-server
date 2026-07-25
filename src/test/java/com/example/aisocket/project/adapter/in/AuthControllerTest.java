package com.example.aisocket.project.adapter.in;

import com.example.aisocket.project.application.dto.command.LoginCommand;
import com.example.aisocket.project.application.dto.command.SignUpMemberCommand;
import com.example.aisocket.project.application.dto.result.LoginResult;
import com.example.aisocket.project.application.dto.result.SignUpMemberResult;
import com.example.aisocket.project.application.in.MemberAuthService;
import com.example.aisocket.project.adapter.in.security.JwtAuthenticationFilter;
import com.example.aisocket.project.application.internal.token.JwtTokenValidator;
import com.example.aisocket.project.application.out.MemberRepository;
import com.example.aisocket.project.config.SecurityConfig;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    private MemberAuthService memberAuthService;

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
        assertThat(commandCaptor.getValue().rawPassword()).isEqualTo("raw-password");
        assertThat(commandCaptor.getValue().nickname()).isEqualTo("runner");
    }

    @Test
    @DisplayName("로그인 요청을 처리한다")
    void login() throws Exception {

        given(memberAuthService.login(any(LoginCommand.class)))
                .willReturn(new LoginResult(
                        1L,
                        "runner@example.com",
                        "runner",
                        "access-token",
                        "refresh-token",
                        Instant.parse("2026-07-24T00:30:00Z"),
                        Instant.parse("2026-08-07T00:00:00Z")
                ));

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestJson()))
                .andExpect(status().isOk())
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

    private String signUpRequestJson() {
        return """
                {
                  "email": "runner@example.com",
                  "password": "raw-password",
                  "nickname": "runner"
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
}
