package com.example.aisocket.project.adapter.out.persistence;

import com.example.aisocket.project.domain.MemberTermsAgreement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberTermsAgreementJpaRepository extends JpaRepository<MemberTermsAgreement, Long> {
}
