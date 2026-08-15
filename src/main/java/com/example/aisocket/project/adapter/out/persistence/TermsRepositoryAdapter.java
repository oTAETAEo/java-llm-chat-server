package com.example.aisocket.project.adapter.out.persistence;

import com.example.aisocket.project.application.out.TermsRepository;
import com.example.aisocket.project.domain.Terms;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TermsRepositoryAdapter implements TermsRepository {

    private final TermsJpaRepository repository;

    @Override
    public List<Terms> findActiveTerms() {
        return repository.findByActiveTrueOrderByRequiredDescIdAsc();
    }

    @Override
    public List<Terms> findActiveRequiredTerms() {
        return repository.findByActiveTrueAndRequiredTrueOrderByIdAsc();
    }

    @Override
    public Optional<Terms> findActiveByCode(String code) {
        return repository.findByCodeAndActiveTrue(code);
    }

    @Override
    public List<Terms> findActiveByIds(Collection<Long> termsIds) {
        if (termsIds == null || termsIds.isEmpty()) {
            return List.of();
        }
        return repository.findByIdInAndActiveTrue(termsIds);
    }
}
