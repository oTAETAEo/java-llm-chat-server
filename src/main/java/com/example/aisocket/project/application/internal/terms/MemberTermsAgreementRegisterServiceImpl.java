package com.example.aisocket.project.application.internal.terms;

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
public class MemberTermsAgreementRegisterServiceImpl implements MemberTermsAgreementRegisterService {

    private final TermsRepository termsRepository;

    private final MemberTermsAgreementRepository memberTermsAgreementRepository;

    @Override
    @Transactional
    public void register(Member member, Collection<Long> agreedTermsIds) {
        Set<Long> agreedIdSet = agreedTermsIds == null ? Set.of() : new HashSet<>(agreedTermsIds);
        List<Terms> requiredTerms = termsRepository.findActiveRequiredTerms();

        boolean agreedAllRequiredTerms = requiredTerms.stream()
                .map(Terms::getId)
                .allMatch(agreedIdSet::contains);
        if (!agreedAllRequiredTerms) {
            throw new ProjectException(MemberErrorCode.REQUIRED_TERMS_NOT_AGREED);
        }

        termsRepository.findActiveByIds(agreedIdSet).stream()
                .map(terms -> MemberTermsAgreement.agree(member, terms))
                .forEach(memberTermsAgreementRepository::save);
    }
}
