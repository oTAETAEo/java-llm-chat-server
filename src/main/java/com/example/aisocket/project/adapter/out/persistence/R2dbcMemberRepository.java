package com.example.aisocket.project.adapter.out.persistence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface R2dbcMemberRepository extends ReactiveCrudRepository<MemberEntity, Long> {
}
