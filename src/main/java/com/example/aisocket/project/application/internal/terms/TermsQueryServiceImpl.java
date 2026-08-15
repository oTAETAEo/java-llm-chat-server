package com.example.aisocket.project.application.internal.terms;

import com.example.aisocket.project.application.dto.result.TermsDetailResult;
import com.example.aisocket.project.application.dto.result.TermsResult;
import com.example.aisocket.project.application.out.TermsRepository;
import com.example.aisocket.project.common.error.ProjectException;
import com.example.aisocket.project.common.error.TermsErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TermsQueryServiceImpl implements TermsQueryService {

    private final TermsRepository termsRepository;

    @Override
    @Transactional(readOnly = true)
    public List<TermsResult> findActiveTerms() {
        return termsRepository.findActiveTerms().stream()
                .map(TermsResult::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TermsDetailResult findActiveTerm(String code) {
        return termsRepository.findActiveByCode(code)
                .map(TermsDetailResult::from)
                .orElseThrow(() -> new ProjectException(TermsErrorCode.TERMS_NOT_FOUND));
    }
}
