package com.example.aisocket.project.adapter.in.security;

import com.example.aisocket.project.application.internal.token.JwtTokenClaims;
import com.example.aisocket.project.application.internal.token.JwtTokenValidator;
import com.example.aisocket.project.application.out.MemberRepository;
import com.example.aisocket.project.domain.Member;
import com.example.aisocket.project.domain.MemberFixture;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class JwtAuthenticationFilterTest {

    private final JwtTokenValidator jwtTokenValidator = mock(JwtTokenValidator.class);
    private final MemberRepository memberRepository = mock(MemberRepository.class);
    private final JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(
            jwtTokenValidator,
            memberRepository
    );

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("액세스 토큰 쿠키가 없으면 인증 없이 다음 필터로 진행한다")
    void doFilterWithoutAccessToken() throws ServletException, IOException {
        MockHttpServletRequest request = request("/api/v1/coach/feedback/single/stream");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(jwtTokenValidator, never()).validate(org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    @DisplayName("인증 엔드포인트는 토큰 검증 필터를 타지 않는다")
    void doFilterWithAuthEndpoint() throws ServletException, IOException {
        MockHttpServletRequest request = request("/api/v1/auth/login");
        request.setCookies(new Cookie("accessToken", "invalid-token"));
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(200);
        verify(jwtTokenValidator, never()).validate(org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    @DisplayName("유효한 액세스 토큰이면 회원을 조회하고 인증 정보를 저장한다")
    void doFilterWithValidAccessToken() throws ServletException, IOException {
        Member member = MemberFixture.builder()
                .id(1L)
                .email("runner@example.com")
                .nickname("runner")
                .build();
        given(jwtTokenValidator.validate("access-token"))
                .willReturn(new JwtTokenClaims(1L, "runner@example.com", "runner", "access"));
        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        MockHttpServletRequest request = request("/api/v1/coach/feedback/single/stream");
        request.setCookies(new Cookie("accessToken", "access-token"));
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(authentication).isNotNull();
        assertThat(authentication.getPrincipal()).isInstanceOf(MemberUserDetails.class);
        MemberUserDetails memberUserDetails = (MemberUserDetails) authentication.getPrincipal();
        assertThat(memberUserDetails.memberId()).isEqualTo(1L);
        assertThat(memberUserDetails.email()).isEqualTo("runner@example.com");
        assertThat(memberUserDetails.nickname()).isEqualTo("runner");
        assertThat(memberUserDetails.getUsername()).isEqualTo("runner@example.com");
        assertThat(memberUserDetails.getAuthorities()).extracting("authority").containsExactly("ROLE_USER");
    }

    @Test
    @DisplayName("액세스 토큰 타입이 아니면 인증에 실패한다")
    void doFilterWithRefreshTokenTypeFails() throws ServletException, IOException {
        given(jwtTokenValidator.validate("refresh-token"))
                .willReturn(new JwtTokenClaims(1L, "runner@example.com", "runner", "refresh"));
        MockHttpServletRequest request = request("/api/v1/coach/feedback/single/stream");
        request.setCookies(new Cookie("accessToken", "refresh-token"));
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("JWT 검증에 실패하면 인증에 실패한다")
    void doFilterWithInvalidTokenFails() throws ServletException, IOException {
        given(jwtTokenValidator.validate("invalid-token"))
                .willThrow(new JwtException("invalid token"));
        MockHttpServletRequest request = request("/api/v1/coach/feedback/single/stream");
        request.setCookies(new Cookie("accessToken", "invalid-token"));
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    private MockHttpServletRequest request(String requestUri) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI(requestUri);
        return request;
    }
}
