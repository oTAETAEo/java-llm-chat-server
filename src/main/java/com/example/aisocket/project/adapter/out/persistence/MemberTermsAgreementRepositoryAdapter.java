package com.example.aisocket.project.adapter.out.persistence;

import com.example.aisocket.project.application.out.MemberTermsAgreementRepository;
import com.example.aisocket.project.domain.MemberTermsAgreement;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberTermsAgreementRepositoryAdapter implements MemberTermsAgreementRepository {

    private final MemberTermsAgreementJpaRepository repository;

    @Override
    public MemberTermsAgreement save(MemberTermsAgreement agreement) {
        return repository.save(agreement);
    }
}
