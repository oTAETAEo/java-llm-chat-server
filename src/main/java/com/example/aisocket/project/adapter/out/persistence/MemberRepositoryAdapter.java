package com.example.aisocket.project.adapter.out.persistence;

import com.example.aisocket.project.application.out.MemberRepository;
import com.example.aisocket.project.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MemberRepositoryAdapter implements MemberRepository {

    private final MemberJpaRepository repository;

    @Override
    public Member save(Member member) {
        return repository.save(member);
    }

    @Override
    public Optional<Member> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Optional<Member> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }
}

