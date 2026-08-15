package com.example.aisocket.project.application.out;

import com.example.aisocket.project.domain.Terms;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TermsRepository {

    List<Terms> findActiveTerms();

    List<Terms> findActiveRequiredTerms();

    Optional<Terms> findActiveByCode(String code);

    List<Terms> findActiveByIds(Collection<Long> termsIds);
}
