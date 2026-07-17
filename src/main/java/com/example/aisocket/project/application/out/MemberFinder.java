package com.example.aisocket.project.application.out;

import com.example.aisocket.project.domain.Member;

import java.util.Optional;

public interface MemberFinder {

    Member save(Member member);

    Optional<Member> findById(Long id);
}
