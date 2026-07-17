package com.example.aisocket.project.adapter.out.persistence;

import com.example.aisocket.project.application.out.MemberRepository;
import com.example.aisocket.project.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class R2dbcMemberPersistenceAdapter implements MemberRepository {

    private final R2dbcMemberRepository repository;

    @Override
    public Mono<Member> save(Member member) {
        return repository.save(MemberEntity.from(member))
                .map(MemberEntity::toDomain);
    }

    @Override
    public Mono<Member> findById(Long id) {
        return repository.findById(id)
                .map(MemberEntity::toDomain);
    }
}
