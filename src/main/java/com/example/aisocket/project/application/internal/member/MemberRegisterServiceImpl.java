package com.example.aisocket.project.application.internal.member;

import com.example.aisocket.project.application.dto.command.RegisterMemberCommand;
import com.example.aisocket.project.application.out.MemberRepository;
import com.example.aisocket.project.domain.Member;
import com.example.aisocket.project.domain.security.PasswordHasher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberRegisterServiceImpl implements MemberRegisterService {

    private final MemberRepository memberRepository;

    private final PasswordHasher passwordHasher;

    @Override
    @Transactional
    public Member register(RegisterMemberCommand command) {
        Member member = Member.create(
                command.email(),
                command.rawPassword(),
                command.nickname(),
                passwordHasher
        );

        return memberRepository.save(member);
    }
}
