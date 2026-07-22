package com.example.aisocket.project.application.internal.member;

import com.example.aisocket.project.application.out.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberFinderServiceImpl implements MemberFinderService {

    private final MemberRepository memberRepository;

    @Override
    @Transactional(readOnly = true)
    public void validateNotExistsByEmail(String email) {
        if (memberRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
    }
}
