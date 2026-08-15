package com.example.aisocket.project.application.out;

import com.example.aisocket.project.domain.MemberTermsAgreement;

import java.util.Set;

public interface MemberTermsAgreementRepository {

    MemberTermsAgreement save(MemberTermsAgreement agreement);

    Set<Long> findAgreedActiveTermsIdsByMemberId(Long memberId);
}
