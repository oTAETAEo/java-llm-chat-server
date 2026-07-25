package com.example.aisocket.project.application.service;

import com.example.aisocket.project.application.dto.command.LoginCommand;
import com.example.aisocket.project.application.dto.command.SignUpMemberCommand;
import com.example.aisocket.project.application.dto.result.LoginResult;
import com.example.aisocket.project.application.dto.result.SignUpMemberResult;
import com.example.aisocket.project.application.in.MemberAuthService;
import com.example.aisocket.project.application.internal.member.MemberFinderService;
import com.example.aisocket.project.application.internal.member.MemberRegisterService;
import com.example.aisocket.project.application.internal.token.IssuedToken;
import com.example.aisocket.project.application.internal.token.RefreshTokenRegisterService;
import com.example.aisocket.project.application.internal.token.JwtTokenProvider;
import com.example.aisocket.project.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
public class MemberAuthServiceImpl implements MemberAuthService {

    private final MemberFinderService memberFinderService;

    private final MemberRegisterService memberRegisterService;

    private final RefreshTokenRegisterService refreshTokenRegisterService;

    private final JwtTokenProvider tokenProvider;

    @Override
    @Transactional
    public SignUpMemberResult signUp(SignUpMemberCommand command) {

        memberFinderService.validateNotExistsByEmail(command.email());

        Member savedMember = memberRegisterService.register(command);

        return new SignUpMemberResult(savedMember.getId(), savedMember.getEmail(), savedMember.getNickname());
    }

    @Override
    @Transactional
    public LoginResult login(LoginCommand command) {

        Member member = memberFinderService.findLoginMember(command);

        IssuedToken issuedToken = tokenProvider.issue(member);

        refreshTokenRegisterService.register(member, issuedToken);

        return new LoginResult(
                member.getId(),
                member.getEmail(),
                member.getNickname(),
                issuedToken.accessToken(),
                issuedToken.refreshToken(),
                issuedToken.accessTokenExpiresAt(),
                issuedToken.refreshTokenExpiresAt()
        );
    }
}
