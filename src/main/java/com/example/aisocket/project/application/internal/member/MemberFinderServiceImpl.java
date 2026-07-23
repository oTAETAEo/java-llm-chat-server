package com.example.aisocket.project.application.internal.member;

import com.example.aisocket.project.application.dto.command.LoginCommand;
import com.example.aisocket.project.application.out.MemberRepository;
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
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Member findLoginMember(LoginCommand command) {

        Member member = memberRepository.findByEmail(command.email())
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다."));

        validatePassword(command, member);

        return member;
    }

    private void validatePassword(LoginCommand command, Member member) {
        if (!passwordHasher.matches(command.rawPassword(), member.getPassword())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }
    }
}
