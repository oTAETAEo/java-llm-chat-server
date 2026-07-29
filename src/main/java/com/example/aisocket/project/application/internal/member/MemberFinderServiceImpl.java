package com.example.aisocket.project.application.internal.member;

import com.example.aisocket.project.application.dto.command.LoginCommand;
import com.example.aisocket.project.application.out.MemberRepository;
import com.example.aisocket.project.common.error.MemberErrorCode;
import com.example.aisocket.project.common.error.ProjectException;
import com.example.aisocket.project.domain.Member;
import com.example.aisocket.project.domain.security.PasswordHasher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberFinderServiceImpl implements MemberFinderService {

    private final MemberRepository memberRepository;

    private final PasswordHasher passwordHasher;

    @Override
    @Transactional(readOnly = true)
    public void validateNotExistsByEmail(String email) {
        if (memberRepository.existsByEmail(email)) {
            throw new ProjectException(MemberErrorCode.DUPLICATED_EMAIL);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Member findLoginMember(LoginCommand command) {

        Member member = memberRepository.findByEmail(command.email())
                .orElseThrow(() -> new ProjectException(MemberErrorCode.INVALID_CREDENTIALS));

        validatePassword(command, member);

        return member;
    }

    @Override
    @Transactional(readOnly = true)
    public Member findById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new ProjectException(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    private void validatePassword(LoginCommand command, Member member) {
        if (!passwordHasher.matches(command.rawPassword(), member.getPassword())) {
            throw new ProjectException(MemberErrorCode.INVALID_CREDENTIALS);
        }
    }
}
