package com.example.aisocket.project.adapter.out.persistence;

import com.example.aisocket.project.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberJpaRepository extends JpaRepository<Member, Long> {
}
