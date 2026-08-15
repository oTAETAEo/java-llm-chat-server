package com.example.aisocket.project.adapter.out.persistence;

import com.example.aisocket.project.domain.MemberTermsAgreement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface MemberTermsAgreementJpaRepository extends JpaRepository<MemberTermsAgreement, Long> {

    @Query("""
            select agreement.terms.id
            from MemberTermsAgreement agreement
            where agreement.member.id = :memberId
              and agreement.terms.active = true
            """)
    Set<Long> findAgreedActiveTermsIdsByMemberId(@Param("memberId") Long memberId);
}
