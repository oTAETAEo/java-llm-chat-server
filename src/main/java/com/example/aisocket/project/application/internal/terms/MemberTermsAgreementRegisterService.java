package com.example.aisocket.project.application.internal.terms;

import com.example.aisocket.project.domain.Member;

import java.util.Collection;

public interface MemberTermsAgreementRegisterService {

    void register(Member member, Collection<Long> agreedTermsIds);
}
