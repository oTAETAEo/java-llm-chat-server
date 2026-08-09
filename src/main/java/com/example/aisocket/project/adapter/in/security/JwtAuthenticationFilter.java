package com.example.aisocket.project.adapter.in.security;

import com.example.aisocket.project.application.internal.token.AccessTokenBlacklistService;
import com.example.aisocket.project.application.internal.token.JwtTokenClaims;
import com.example.aisocket.project.application.internal.token.JwtTokenValidator;
import com.example.aisocket.project.application.out.MemberRepository;
import com.example.aisocket.project.common.error.ErrorResponse;
import com.example.aisocket.project.common.error.MemberErrorCode;
import com.example.aisocket.project.common.error.ProjectException;
import com.example.aisocket.project.common.error.TokenErrorCode;
import com.example.aisocket.project.domain.Member;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String ACCESS_TOKEN_COOKIE_NAME = "accessToken";
    private static final String AUTH_ENDPOINT_PREFIX = "/api/v1/auth/";

    private final JwtTokenValidator jwtTokenValidator;

    private final AccessTokenBlacklistService accessTokenBlacklistService;

    private final MemberRepository memberRepository;

    private final ObjectMapper objectMapper;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getRequestURI().startsWith(AUTH_ENDPOINT_PREFIX);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        Optional<String> accessToken = extractAccessToken(request);
        if (accessToken.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            JwtTokenClaims claims = jwtTokenValidator.validateAccessToken(accessToken.get());
            validateNotBlacklisted(accessToken.get());

            Member member = memberRepository.findById(claims.memberId())
                    .orElseThrow(() -> new ProjectException(MemberErrorCode.AUTHENTICATED_MEMBER_NOT_FOUND));

            saveAuthentication(request, member);
            filterChain.doFilter(request, response);
        } catch (JwtException | ProjectException | IllegalArgumentException exception) {
            SecurityContextHolder.clearContext();
            sendAuthenticationError(request, response);
        }
    }

    private void sendAuthenticationError(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        objectMapper.writeValue(
                response.getWriter(),
                ErrorResponse.of(TokenErrorCode.AUTHENTICATION_FAILED, request.getRequestURI())
        );
    }

    private Optional<String> extractAccessToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return Optional.empty();
        }

        return Arrays.stream(cookies)
                .filter(cookie -> ACCESS_TOKEN_COOKIE_NAME.equals(cookie.getName()))
                .map(Cookie::getValue)
                .filter(value -> !value.isBlank())
                .findFirst();
    }

    private void validateNotBlacklisted(String accessToken) {
        if (accessTokenBlacklistService.isBlacklisted(accessToken)) {
            throw new ProjectException(TokenErrorCode.BLACKLISTED_ACCESS_TOKEN);
        }
    }

    private void saveAuthentication(HttpServletRequest request, Member member) {
        MemberUserDetails memberUserDetails = new MemberUserDetails(member);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                memberUserDetails,
                null,
                memberUserDetails.getAuthorities()
        );
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
