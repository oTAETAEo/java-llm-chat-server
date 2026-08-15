package com.example.aisocket.project.application.internal.terms;

import com.example.aisocket.project.application.dto.result.TermsAgreementStatusResult;

import java.util.Collection;

public interface TermsAgreementStatusService {

    TermsAgreementStatusResult findStatus(Long memberId);

    TermsAgreementStatusResult agree(Long memberId, Collection<Long> agreedTermsIds);
}
