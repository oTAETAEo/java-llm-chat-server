package com.example.aisocket.project.adapter.out.persistence;

import com.example.aisocket.project.application.out.MemberRepository;
import com.example.aisocket.project.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JpaMemberPersistenceAdapter implements MemberRepository {

    private final JpaMemberRepository repository;

    @Override
    public Member save(Member member) {
        return repository.save(MemberEntity.from(member))
                .toDomain();
    }

    @Override
    public Optional<Member> findById(Long id) {
        return repository.findById(id)
                .map(MemberEntity::toDomain);
    }
}
