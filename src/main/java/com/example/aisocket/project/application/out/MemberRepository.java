package com.example.aisocket.project.application.out;

import com.example.aisocket.project.domain.Member;
import reactor.core.publisher.Mono;

public interface MemberRepository {

    Mono<Member> save(Member member);

    Mono<Member> findById(Long id);
}
