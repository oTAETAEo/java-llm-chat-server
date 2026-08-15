package com.example.aisocket.project.application.internal.terms;

import com.example.aisocket.project.application.dto.result.TermsResult;
import com.example.aisocket.project.application.dto.result.TermsDetailResult;

import java.util.List;

public interface TermsQueryService {

    List<TermsResult> findActiveTerms();

    TermsDetailResult findActiveTerm(String code);
}
