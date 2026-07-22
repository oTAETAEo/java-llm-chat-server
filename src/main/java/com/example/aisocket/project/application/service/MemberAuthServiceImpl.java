package com.example.aisocket.project.application.service;

import com.example.aisocket.project.application.dto.command.RegisterMemberCommand;
import com.example.aisocket.project.application.dto.result.RegisterMemberResult;
import com.example.aisocket.project.application.in.MemberAuthService;
import com.example.aisocket.project.application.internal.member.MemberFinderService;
import com.example.aisocket.project.application.internal.member.MemberRegisterService;
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

    @Override
    @Transactional
    public RegisterMemberResult register(RegisterMemberCommand command) {
        memberFinderService.validateNotExistsByEmail(command.email());

        Member savedMember = memberRegisterService.register(command);

        return new RegisterMemberResult(savedMember.getId(), savedMember.getEmail(), savedMember.getNickname());
    }
}
