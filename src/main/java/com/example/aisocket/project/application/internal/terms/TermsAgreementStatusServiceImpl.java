package com.example.aisocket.project.application.internal.terms;

import com.example.aisocket.project.application.dto.result.TermsAgreementStatusResult;
import com.example.aisocket.project.application.dto.result.TermsResult;
import com.example.aisocket.project.application.internal.member.MemberFinderService;
import com.example.aisocket.project.application.out.MemberTermsAgreementRepository;
import com.example.aisocket.project.application.out.TermsRepository;
import com.example.aisocket.project.common.error.MemberErrorCode;
import com.example.aisocket.project.common.error.ProjectException;
import com.example.aisocket.project.domain.Member;
import com.example.aisocket.project.domain.MemberTermsAgreement;
import com.example.aisocket.project.domain.Terms;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TermsAgreementStatusServiceImpl implements TermsAgreementStatusService {

    private final MemberFinderService memberFinderService;

    private final TermsRepository termsRepository;

    private final MemberTermsAgreementRepository memberTermsAgreementRepository;

    @Override
    @Transactional(readOnly = true)
    public TermsAgreementStatusResult findStatus(Long memberId) {
        Set<Long> agreedTermIds = memberTermsAgreementRepository.findAgreedActiveTermsIdsByMemberId(memberId);

        return toStatus(agreedTermIds);
    }

    @Override
    @Transactional
    public TermsAgreementStatusResult agree(Long memberId, Collection<Long> agreedTermsIds) {
        Member member = memberFinderService.findById(memberId);
        Set<Long> previousAgreedTermIds = memberTermsAgreementRepository.findAgreedActiveTermsIdsByMemberId(memberId);
        Set<Long> requestedTermIds = agreedTermsIds == null ? Set.of() : new HashSet<>(agreedTermsIds);
        Set<Long> nextAgreedTermIds = new HashSet<>(previousAgreedTermIds);
        nextAgreedTermIds.addAll(requestedTermIds);

        TermsAgreementStatusResult nextStatus = toStatus(nextAgreedTermIds);
        if (nextStatus.requiresTermsAgreement()) {
            throw new ProjectException(MemberErrorCode.REQUIRED_TERMS_NOT_AGREED);
        }

        termsRepository.findActiveByIds(requestedTermIds).stream()
                .filter(terms -> !previousAgreedTermIds.contains(terms.getId()))
                .map(terms -> MemberTermsAgreement.agree(member, terms))
                .forEach(memberTermsAgreementRepository::save);

        return toStatus(memberTermsAgreementRepository.findAgreedActiveTermsIdsByMemberId(memberId));
    }

    private TermsAgreementStatusResult toStatus(Set<Long> agreedTermIds) {
        List<TermsResult> missingRequiredTerms = termsRepository.findActiveRequiredTerms().stream()
                .filter(terms -> !agreedTermIds.contains(terms.getId()))
                .map(TermsResult::from)
                .toList();

        return new TermsAgreementStatusResult(!missingRequiredTerms.isEmpty(), missingRequiredTerms);
    }
}
